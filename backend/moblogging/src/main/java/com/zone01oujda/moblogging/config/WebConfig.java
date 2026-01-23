package com.zone01oujda.moblogging.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${files.uploadDirectory}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String webPath = uploadDir.startsWith("/") ? uploadDir : "/" + uploadDir;
        String normalizedWeb = webPath.endsWith("/") ? webPath : webPath + "/";
        String filePath = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        registry.addResourceHandler(normalizedWeb + "**")
                .addResourceLocations("file:" + filePath);
    }
}
