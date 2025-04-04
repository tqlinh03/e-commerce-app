package com.linh.ecommerce.cloudflare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/images")
public class ImageUploadController {

    @Autowired
    private R2UploadService r2UploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Kiểm tra định dạng file
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                return ResponseEntity.status(400).body("File must be an image");
            }

            // Kiểm tra kích thước file (giới hạn 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return ResponseEntity.status(400).body("File size exceeds 5MB limit");
            }

            // Lưu file tạm thời
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            String key = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String imageUrl = r2UploadService.uploadImage(tempFile, key);

            tempFile.delete();
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        }
    }
}