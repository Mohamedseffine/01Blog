package com.zone01oujda.moblogging.util;


import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {

    private final MediaValidator MediaValidator;

    public FileUploadUtil(MediaValidator mediaValidator) {
        this.MediaValidator = mediaValidator;
    }

    public String upload(MultipartFile file) {
        try{
            String type = MediaValidator.detectMimeType(file);
            return type;
        }catch (IOException e) {
            return "";
        }
    }  
}
