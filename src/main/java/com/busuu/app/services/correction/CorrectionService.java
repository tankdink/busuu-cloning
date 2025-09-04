package com.busuu.app.services.correction;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.correction.CorrectionDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.CorrectionResponse;
import com.busuu.app.entities.Correction;
import com.busuu.app.entities.User;
import com.busuu.app.entities.post.Post;
import com.busuu.app.entities.post.PostType;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.InvalidFileException;
import com.busuu.app.repositories.CorrectionRepository;
import com.busuu.app.repositories.CorrectionRepository;
import com.busuu.app.repositories.LanguageRepository;
import com.busuu.app.repositories.PostRepository;
import com.busuu.app.services.cloudinary.IUploadCloudinaryService;
import com.busuu.app.specification.CorrectionSpecification;
import com.busuu.app.utils.UploadCloudinaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorrectionService implements ICorrectionService
{
    
    private final CorrectionRepository correctionRepository;

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    private final IUploadCloudinaryService uploadCloudinaryService;

    
    @Override
    public CorrectionResponse insertCorrection(String requestId, CorrectionDTO correctionDTO) 
    {
        try {

            Correction newCorrection = modelMapper.map(correctionDTO, Correction.class);
            newCorrection.setId(UUID.randomUUID().toString());

            //Valid postId
            Post existingPost = postRepository.findById(correctionDTO.getPostId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find post with ID " + correctionDTO.getPostId()) );

            //Exception
            if (existingPost.getPostType() == PostType.TEXT && ( correctionDTO.getCorrectionText() == null || correctionDTO.getCorrectionText().isEmpty() ))
                throw new IllegalArgumentException("Post with TEXT type cannot have null Correction text");
            if (existingPost.getPostType() == PostType.AUDIO && correctionDTO.getCorrectionAudio() == null)
                throw new IllegalArgumentException("Post with AUDIO type cannot have null Correction audio");


            //Check valid file
            if (correctionDTO.getCorrectionAudio() != null) checkFile(correctionDTO.getCorrectionAudio(), "audio");

            //MultipartFile process
            CloudinaryResponse cloudinaryResponse = null;


            //Correction audio
            if (correctionDTO.getCorrectionAudio() != null) cloudinaryResponse = uploadFile(correctionDTO.getCorrectionAudio());
            if (cloudinaryResponse != null) {
                newCorrection.setCorrectionAudioUrl(cloudinaryResponse.getUrl());
                newCorrection.setCorrectionAudioName(cloudinaryResponse.getPublicId());
            }

            //Get and set user/post
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            newCorrection.setUser(user);
            newCorrection.setPost(existingPost);

            //Save and map return
            newCorrection = correctionRepository.save(newCorrection);
            CorrectionResponse correctionResponse = modelMapper.map(newCorrection, CorrectionResponse.class);
            correctionResponse.setUserId(user.getId());
            correctionResponse.setPostId(newCorrection.getPost().getId());
            return correctionResponse;

        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create new correction, err= "+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_CORRECTION, requestId);
        }
    }

    @Override
    public CorrectionResponse getCorrection(String requestId, String correctionId)
    {
        try
        {

            Correction correction= correctionRepository.findById(correctionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Correction with ID = " + correctionId));

            CorrectionResponse correctionResponse = modelMapper.map(correction, CorrectionResponse.class);
            correctionResponse.setUserId(correction.getUser().getId());
            correctionResponse.setPostId(correction.getPost().getId());

            return correctionResponse;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get Correction, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_CORRECTION, requestId);
        }
    }

    @Override
    public List<CorrectionResponse> getByUserId(String requestId, String userId)
    {
        try {

            List<Correction> corrections = correctionRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt"));

            return corrections.stream().map(correction ->
            {
                CorrectionResponse correctionResponse = modelMapper.map(correction, CorrectionResponse.class);
                correctionResponse.setUserId(correction.getUser().getId());
                correctionResponse.setPostId(correction.getPost().getId());

                return correctionResponse;

            }).toList();

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get Correction list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_CORRECTION, requestId);
        }
    }

    @Override
    public List<CorrectionResponse> getByPostId(String requestId, String postId)
    {
        try {

            List<Correction> corrections = correctionRepository.findByPostId(postId, Sort.by(Sort.Direction.DESC, "createdAt"));

            return corrections.stream().map(correction ->
            {
                CorrectionResponse correctionResponse = modelMapper.map(correction, CorrectionResponse.class);
                correctionResponse.setUserId(correction.getUser().getId());
                correctionResponse.setPostId(correction.getPost().getId());

                return correctionResponse;

            }).toList();

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get Correction list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_CORRECTION, requestId);
        }
    }

    @Override
    public Page<CorrectionResponse> getSelfCorrection(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String language)
    {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();

            Pageable pageable = PageRequest.of(page, size);

            Page<Correction> corrections = correctionRepository.findAll(CorrectionSpecification.getSpecification(language, sortBy, sortDirection, user.getId()), pageable);

            return corrections.map(correction ->
            {
                CorrectionResponse correctionResponse = modelMapper.map(correction, CorrectionResponse.class);
                correctionResponse.setUserId(correction.getUser().getId());
                correctionResponse.setPostId(correction.getPost().getId());

                return correctionResponse;

            });

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get corrections, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_POST, requestId);
        }
    }

    private CloudinaryResponse uploadFile(MultipartFile file) throws Exception
    {
        //Get file type
        String contentType = file.getContentType();

        String resourceType = contentType.split("/")[0];

        //Upload
        UploadCloudinaryUtil.assertAllowed(file, resourceType);
        String fileName = UploadCloudinaryUtil.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = uploadCloudinaryService.uploadFile(file, fileName, resourceType);
        return response;
    }

    private void checkFile(MultipartFile file, String requiredType) throws Exception
    {
        //Get file type
        String contentType = file.getContentType(); //Return like e.g. "image/png", "video/mp4", ...
        if (contentType == null || !contentType.contains("/")) {
            throw new InvalidFileException("Unsupported file type: " + contentType);
        }

        String resourceType = contentType.split("/")[0]; //e.g. "image", "video", "application"

        if (resourceType.equals("application") || resourceType.equals("text")) throw new InvalidFileException("Unsupported file type: " + resourceType);
        if (!resourceType.equals(requiredType)) throw new InvalidFileException("Invalid file type: required: " + requiredType + " but get: " + resourceType);

    }
}
