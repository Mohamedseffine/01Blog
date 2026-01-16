package com.zone01oujda.moblogging.util;

import java.io.File;

import org.springframework.stereotype.Component;

@Component
public class FileUploadUtil {

    private final MediaValidator MediaValidator;

    public FileUploadUtil(MediaValidator mediaValidator) {
        this.MediaValidator = mediaValidator;
    }

    public String upload(File file) {
        
        return file.getName();
    }  
}
