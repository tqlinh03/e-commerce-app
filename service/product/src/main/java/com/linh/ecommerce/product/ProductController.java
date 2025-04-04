package com.linh.ecommerce.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ObjectMapper objectMapper;
    private final ProductService service;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ProductResponse> create(
            @RequestPart("product") String productJson,
            @RequestPart("image") MultipartFile image
    ) throws Exception {
        ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);

        return ResponseEntity.ok(service.create(request, image));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductResponse>> getByStoreId(@PathVariable UUID storeId) {
        return ResponseEntity.ok(service.getByStoreId(storeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID id,
            @RequestPart("product") String productJson,
            @RequestPart("image") MultipartFile image
    ) throws Exception {
        ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
        return ResponseEntity.ok(service.update(id, request, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
} 