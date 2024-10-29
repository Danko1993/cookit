package com.example.cookit.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${file.upload-dir.ingredient}")
    private String ingredientUploadDir;

    @Value("${file.upload-dir.meal}")
    private String mealUploadDir;

    @Value("${file.upload-dir.appUser}")
    private String appUserUploadDir;

    public String saveFile(MultipartFile file, String entity) throws IOException {
        String uploadDir;
        switch (entity.toLowerCase()) {
            case "meal":
                uploadDir = mealUploadDir;
                break;
            case "ingredient":
                uploadDir = ingredientUploadDir;
                break;
            case "appuser":
                uploadDir = appUserUploadDir;
                break;
            default:
                throw new IllegalArgumentException("Unknown entity type " + entity);
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        String fileName = UUID.randomUUID().toString().substring(0,5) + originalFilename;
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }

    public void deleteFile(String path) throws IOException {
        Files.delete(Paths.get(path));
    }
}
