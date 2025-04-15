package com.linh.ecommerce.product;

import com.linh.ecommerce.cloudflare.R2UploadService;
import com.linh.ecommerce.exception.ProductPurchaseException;
import com.linh.ecommerce.inventory.Inventory;
import com.linh.ecommerce.inventory.InventoryMapper;
import com.linh.ecommerce.inventory.InventoryRepository;
import com.linh.ecommerce.size.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final R2UploadService r2UploadService;
    private final SizeRepository sizeRepository;

    @Transactional
    public ProductResponse create(ProductRequest request, MultipartFile image) throws Exception {
        Product product = productMapper.toProduct(request);
        Product savedProduct = productRepository.save(product);
        
        List<Inventory> inventories = inventoryMapper.toInventories(savedProduct, request.inventories());
        List<Inventory> savedInventories = inventoryRepository.saveAll(inventories);

        // Tải ảnh lên R2 nếu có
        if (image != null) {
            String key = UUID.randomUUID() + "-" + image.getOriginalFilename();
            String imageUrl = r2UploadService.uploadImage(image, key);
            product.setImageUrl(imageUrl);
        }

        return productMapper.toProductResponse(savedProduct, savedInventories);
    }

    public ProductResponse getById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        List<Inventory> inventories = inventoryRepository.findByProductId(id);
        return productMapper.toProductResponse(product, inventories);
    }

    public List<ProductResponse> getByStoreId(UUID storeId) {
        List<Product> products = productRepository.findByStoreId(storeId);
        return products.stream()
                .map(product -> {
                    List<Inventory> inventories = inventoryRepository.findByProductId(product.getId());
                    return productMapper.toProductResponse(product, inventories);
                })
                .toList();
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request, MultipartFile image) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));


        Product updatedProduct = productMapper.updateProduct(product, request);
        Product savedProduct = productRepository.save(updatedProduct);
        
        // Update inventories
        inventoryRepository.deleteByProductId(id);
        List<Inventory> newInventories = inventoryMapper.toInventories(savedProduct, request.inventories());
        List<Inventory> savedInventories = inventoryRepository.saveAll(newInventories);

        // Cập nhật ảnh nếu có
        if (image != null) {
            String key;
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                // Nếu sản phẩm đã có ảnh, lấy key từ URL để ghi đè
                key = product.getImageUrl().substring(product.getImageUrl().lastIndexOf("/") + 1);
            } else {
                // Nếu không có ảnh trước đó, tạo key mới
                key = UUID.randomUUID() + "-" + image.getOriginalFilename();
            }

            if(key.equals(product.getImageUrl())) {
            }
            String imageUrl = r2UploadService.uploadImage(image, key);
            product.setImageUrl(imageUrl);
        }
        return productMapper.toProductResponse(savedProduct, savedInventories);
    }

    @Transactional
    public void delete(UUID id) {
        inventoryRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    @Transactional(rollbackFor = ProductPurchaseException.class)
    public List<ProductPurchaseResponse> purchaseProducts(
            List<ProductPurchaseRequest> request
    ) {
        // Trích xuất danh sách productId từ request
        var productIds = request.stream().map(ProductPurchaseRequest::productId).toList();

        // Kiểm tra sản phẩm tồn tại
        var storedProducts = productRepository.findAllByIdInOrderById(productIds);

        // Kiểm tra xem tất cả sản phẩm có tồn tại không
//        if (productIds.size() != storedProducts.size()) {
//            throw new ProductPurchaseException("One or more products does not exist");
//        }

        // Danh sách để lưu kết quả các sản phẩm đã mua
        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();

        // Xử lý từng sản phẩm trong danh sách
        for (ProductPurchaseRequest productRequest : request) {
            // Tìm sản phẩm trong danh sách đã lấy từ DB
            var product = storedProducts.stream()
                    .filter(p -> p.getId().equals(productRequest.productId()))
                    .findFirst()
                    .orElseThrow(() -> new ProductPurchaseException("Product not found: " + productRequest.productId()));

            // Kiểm tra xem sản phẩm có size hay không
            UUID sizeId = productRequest.sizeId();
            Inventory inventory;

            if (sizeId != null) {
                // Sản phẩm có size
                
                // Kiểm tra xem size có tồn tại cho sản phẩm này không
                boolean sizeExists = productRepository.existsByProductIdAndSizeId(productRequest.productId(), sizeId);
                if (!sizeExists) {
                    throw new ProductPurchaseException(
                            "Product with ID " + productRequest.productId() + " does not have size with ID " + sizeId
                    );
                }
                
                // Lấy inventory cho product và size
                inventory = inventoryRepository.findByProductIdAndSizeId(productRequest.productId(), sizeId)
                        .orElseThrow(() -> new ProductPurchaseException(
                                "Inventory not found for product ID " + productRequest.productId() + " and size ID " + sizeId
                        ));
            } else {
                // Sản phẩm không có size - tìm inventory chỉ với productId và sizeId là null
                inventory = inventoryRepository.findByProductIdAndSizeIdIsNull(productRequest.productId())
                        .orElseThrow(() -> new ProductPurchaseException(
                                "Inventory not found for product ID " + productRequest.productId() + " without size"
                        ));
            }

            // Kiểm tra số lượng tồn kho
            if (inventory.getQuantity() < productRequest.quantity()) {
                String errorMessage = "Insufficient stock quantity for product with ID " + productRequest.productId();
                if (sizeId != null) {
                    errorMessage += " and size ID " + sizeId;
                }
                throw new ProductPurchaseException(errorMessage);
            }

            // Cập nhật số lượng tồn kho mới
            var newQuantity = inventory.getQuantity() - productRequest.quantity();
            inventory.setQuantity(newQuantity);

            // Lưu thay đổi vào database
            inventoryRepository.save(inventory);

            // Chuyển đổi sang response và thêm vào danh sách kết quả
            purchasedProducts.add(productMapper.toProductPurchaseResponse(product, inventory, productRequest.quantity()));
        }

        // Trả về danh sách sản phẩm đã mua
        return purchasedProducts;
    }
} 