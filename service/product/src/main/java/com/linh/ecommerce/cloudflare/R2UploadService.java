package com.linh.ecommerce.cloudflare;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.net.URI;
import java.util.List;

@Service
public class R2UploadService {
    @Value("${spring.application.r2.bucket.name}")
    private String bucketName;

    @Value("${spring.application.r2.domain}")
    private String domain;

    @Value("${spring.application.r2.credentials.access-key}")
    private String accessKey;

    @Value("${spring.application.r2.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.application.r2.credentials.account-id}")
    private String accountId;

    private S3Client s3Client;

    // Khởi tạo S3Client khi service được tạo
    @PostConstruct
    public void init() {
        // Kiểm tra thông tin xác thực
        if (accessKey == null || secretKey == null || accountId == null || bucketName == null) {
            throw new IllegalStateException("Missing R2 configuration in application.yml");
        }

        String r2Endpoint = "https://" + accountId + ".r2.cloudflarestorage.com";
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // Khởi tạo S3Client với endpoint của R2
        s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(r2Endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    public void validateImageFile(MultipartFile file) {
        // Kiểm tra file không null hoặc rỗng
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Kiểm tra định dạng file (chỉ cho phép JPEG và PNG)
        String contentType = file.getContentType();
        if (contentType == null || !List.of("image/jpeg", "image/png").contains(contentType)) {
            throw new IllegalArgumentException("File must be a JPEG or PNG image");
        }

        // Kiểm tra kích thước file (giới hạn 5MB)
        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds " + (maxFileSize / (1024 * 1024)) + "MB limit");
        }
    }

    public String uploadImage(File file, String key) {
        // Tạo yêu cầu tải lên
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        // Tải ảnh lên
        s3Client.putObject(request, file.toPath());

        // Trả về URL công khai của ảnh (nếu bucket là public)
        return "https://" + domain + "/" + key;
    }

    public String uploadImage(MultipartFile image, String key) throws Exception {
        // Kiểm tra file ảnh
        validateImageFile(image);

        // Lưu file tạm thời trên server
        File tempFile = File.createTempFile("upload-", image.getOriginalFilename());
        try {
            image.transferTo(tempFile);
            return uploadImage(tempFile, key);
        } finally {
            // Xóa file tạm sau khi tải lên
            tempFile.delete();
        }
    }
}
