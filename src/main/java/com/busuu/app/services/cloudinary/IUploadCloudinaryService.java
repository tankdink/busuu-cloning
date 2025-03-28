package com.busuu.app.services.cloudinary;

import com.busuu.app.dtos.responses.CloudinaryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadCloudinaryService {

    CloudinaryResponse uploadFile (MultipartFile file, String fileName, String type) throws Exception;

    boolean removeFile (String publicId) throws Exception;
}
