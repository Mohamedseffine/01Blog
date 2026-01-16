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
        try{
            String type = mediaValidator.detectMimeType(file);
            System.out.println(type);
            if (type.startsWith("image") || type.startsWith("video")) {
                Path path = Paths.get("." + uploadDir);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                String newName = mediaValidator.generateSafeFileName();
                Path uploadPath = path.resolve(newName + file.getOriginalFilename());
                Files.write(uploadPath, file.getBytes());
                return uploadPath.toString();
            }
            throw new RuntimeException("unsupported filetype");
        }catch (IOException e) {
            throw new RuntimeException("Error Reading file");
        }
    }  
}
