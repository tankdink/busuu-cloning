package com.busuu.app.utils;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadCloudinaryUtil {
    public static final long MAX_FILE_SIZE = 50 * 1024 * 1024;
    public static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp))$)";
    public static final String VIDEO_PATTERN = "([^\\s]+(\\.(?i)(mp4|avi|mov|wmv|flv))$)";
    public static final String AUDIO_PATTERN = "([^\\s]+(\\.(?i)(mp3|wav|ogg|aac|flac))$)";

    public static boolean isAllowedExtension(String fileName, String pattern) {
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public static void assertAllowed(MultipartFile file, String type) throws Exception {
        long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new Exception("Max file size is 50MB");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new Exception("Invalid file name");
        }

        switch (type.toLowerCase()) {
            case "image" -> {
                if (!isAllowedExtension(fileName, IMAGE_PATTERN)) {
                    throw new Exception("Only JPG, PNG, GIF, BMP images are allowed");
                }
            }
            case "video" -> {
                if (!isAllowedExtension(fileName, VIDEO_PATTERN)) {
                    throw new Exception("Only MP4, AVI, MOV, WMV, FLV videos are allowed");
                }
            }
            case "audio" -> {
                if (!isAllowedExtension(fileName, AUDIO_PATTERN)) {
                    throw new Exception("Only MP3, WAV, OGG, AAC, FLAC audio files are allowed");
                }
            }
            default -> throw new Exception("Invalid file type. Only image, video, and audio are supported.");
        }
    }

    public static String getFileName(String name) {
        return UUID.randomUUID().toString() + "_" + name;
    }

    public static String getFolderByType(String type) {
        return switch (type.toLowerCase()) {
            case "image" -> "busuu/images";
            case "video" -> "busuu/videos";
            case "audio" -> "busuu/audios";
            default -> "busuu/others";
        };
    }
}
