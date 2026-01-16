package com.zone01oujda.moblogging.util;


import java.io.IOException;

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
            System.out.println(uploadDir);
            String type = mediaValidator.detectMimeType(file);
            System.out.println(type);
            return type;
        }catch (IOException e) {
            return "";
        }
    }  
}
