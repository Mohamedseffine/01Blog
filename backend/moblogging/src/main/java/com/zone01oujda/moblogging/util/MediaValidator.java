package com.zone01oujda.moblogging.util;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

/**
 * Utility class for validating and processing media files
 */
@Component
public class MediaValidator {
    
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
        "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo", "video/webm"
    );
    
    private Tika tika;
    private long maxFileSize;

    public MediaValidator(@Value("${files.maxSize:52428800}") long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @PostConstruct
    public void init() {
        this.tika = new Tika();
    }

    /**
     * Detect MIME type of a file
     * @param file the file to detect MIME type for
     * @return the detected MIME type
     * @throws IOException if an error occurs while reading the file
     * @throws IllegalArgumentException if file is null
     */
    public String detectMimeType(MultipartFile file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        return tika.detect(file.getBytes(), file.getOriginalFilename());
    }

    /**
     * Generate a safe file name using UUID
     * @return a safe file name
     */
    public String generateSafeFileName() {
        return "_" + UUID.randomUUID() + "_";
    }

    /**
     * Validate if file is an image
     * @param mimeType the MIME type to validate
     * @return true if the MIME type is a supported image type
     */
    public boolean isValidImageType(String mimeType) {
        return ALLOWED_IMAGE_TYPES.contains(mimeType);
    }

    /**
     * Validate if file is a video
     * @param mimeType the MIME type to validate
     * @return true if the MIME type is a supported video type
     */
    public boolean isValidVideoType(String mimeType) {
        return ALLOWED_VIDEO_TYPES.contains(mimeType);
    }

    /**
     * Validate file size
     * @param file the file to validate
     * @return true if file size is within the allowed limit
     */
    public boolean isValidFileSize(MultipartFile file) {
        return file != null && file.getSize() <= maxFileSize;
    }

    /**
     * Validate if file is empty
     * @param file the file to validate
     * @return true if file is not empty
     */
    public boolean isNotEmpty(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    /**
     * Get max allowed file size in bytes
     * @return max file size
     */
    public long getMaxFileSize() {
        return maxFileSize;
    }
}
