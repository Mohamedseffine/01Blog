package com.zone01oujda.moblogging.util;


import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Component
public class MediaValidator {
    private Tika tika;

    @PostConstruct
    public void init() {
        this.tika = new Tika();
    }

    public String detectMimeType(MultipartFile file) throws java.io.IOException{
        return tika.detect(file.getBytes(), file.getOriginalFilename());
    }
    public String generateSafeFileName() {
        return "_"+ UUID.randomUUID() +"_";
    }
    
}
