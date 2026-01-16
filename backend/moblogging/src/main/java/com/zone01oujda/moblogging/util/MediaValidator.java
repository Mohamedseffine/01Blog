package com.zone01oujda.moblogging.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MediaValidator {
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpeg", "jpg", "png", "gif", "bmp", "webp");

    private static final List ALLOWED_VIDEO_EXTENSIONS = Arrays.asList("mp4", "avi", "mov", "mvw", "flv");
    
}
