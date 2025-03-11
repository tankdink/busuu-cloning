package com.busuu.app.services.cloudinary;

import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.utils.UploadCloudinaryUtil;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadCloudinaryService implements IUploadCloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    @Transactional
    public CloudinaryResponse uploadFile(MultipartFile file, String fileName, String type) throws Exception {
        try {
            int lastDotIndex = fileName.lastIndexOf(".");
            if (lastDotIndex != -1) {
                fileName = fileName.substring(0, lastDotIndex);
            }

            String folder = UploadCloudinaryUtil.getFolderByType(type);
            Map result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "public_id", fileName,
                            "folder", folder
                    ));

            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            return CloudinaryResponse.builder()
                    .publicId(publicId)
                    .url(url)
                    .build();
        } catch (Exception e) {
            throw new Exception("Failed to upload file");
        }
    }

    @Override
    @Transactional
    public boolean removeFile(String publicId) throws Exception {
        try {
            Map result = cloudinary.uploader().destroy(publicId, Map.of("invalidate", true));
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            throw new Exception("Failed to delete file");
        }
    }
}
