package com.linh.ecommerce.product;

import com.linh.ecommerce.cloudflare.R2UploadService;
import com.linh.ecommerce.inventory.Inventory;
import com.linh.ecommerce.inventory.InventoryMapper;
import com.linh.ecommerce.inventory.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
} 