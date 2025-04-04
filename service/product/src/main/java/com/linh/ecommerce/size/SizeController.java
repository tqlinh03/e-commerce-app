package com.linh.ecommerce.size;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sizes")
@RequiredArgsConstructor
public class SizeController {

    private final SizeService sizeService;

    // Create
    @PostMapping
    public ResponseEntity<SizeResponse> createSize(@RequestBody SizeRequest request) {
        SizeResponse response = sizeService.createSize(request);
        return ResponseEntity.ok(response);
    }

    // Read (Get by ID)
    @GetMapping("/{id}")
    public ResponseEntity<SizeResponse> getSizeById(@PathVariable UUID id) {
        SizeResponse response = sizeService.getSizeById(id);
        return ResponseEntity.ok(response);
    }

    // Read (Get all)
    @GetMapping
    public ResponseEntity<List<SizeResponse>> getAllSizes() {
        List<SizeResponse> responses = sizeService.getAllSizes();
        return ResponseEntity.ok(responses);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<SizeResponse> updateSize(
            @PathVariable UUID id, @RequestBody SizeRequest request) {
        SizeResponse response = sizeService.updateSize(id, request);
        return ResponseEntity.ok(response);
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSize(@PathVariable UUID id) {
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }
}
