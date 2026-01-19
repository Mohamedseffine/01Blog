package com.zone01oujda.moblogging.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {

    private final String uploadDir;
    private final MediaValidator mediaValidator;

    public FileUploadUtil(
            MediaValidator mediaValidator,
            @Value("${files.uploadDirectory}") String uploadDir) {
        this.mediaValidator = mediaValidator;
        this.uploadDir = uploadDir;
    }

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        try {
            String type = mediaValidator.detectMimeType(file);
            String subDir = type.startsWith("image") ? "/images" : "/videos";
            
            if (!type.startsWith("image") && !type.startsWith("video")) {
                throw new IllegalArgumentException("Unsupported file type: " + type);
            }
            
            Path path = Paths.get(".", uploadDir, subDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            
            String newName = mediaValidator.generateSafeFileName();
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            
            Path uploadPath = path.resolve(newName + fileExtension);
            Files.write(uploadPath, file.getBytes());
            
            return uploadPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error reading/writing file: " + e.getMessage(), e);
        }
    }
    
    public void delete(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file: " + e.getMessage(), e);
        }
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
