package com.busuu.app.services.post;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.post.PostDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.PostResponse;
import com.busuu.app.entities.Language;
import com.busuu.app.entities.User;
import com.busuu.app.entities.post.Post;
import com.busuu.app.entities.post.PostType;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.InvalidFileException;
import com.busuu.app.repositories.LanguageRepository;
import com.busuu.app.repositories.PostRepository;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.services.cloudinary.IUploadCloudinaryService;
import com.busuu.app.specification.PostSpecification;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService implements IPostService
{

    private final ModelMapper modelMapper;

    private final IUploadCloudinaryService uploadCloudinaryService;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final LanguageRepository languageRepository;

    @Override
    @Transactional
    public PostResponse insertPost(String requestId, PostDTO postDTO)
    {
        try {

            Post newPost = modelMapper.map(postDTO, Post.class);
            newPost.setId(UUID.randomUUID().toString());
            newPost.setPostType(PostType.valueOf(postDTO.getPostTypes().toUpperCase()));

            //Exception
            if (newPost.getPostType() == PostType.TEXT && ( postDTO.getPostText() == null || postDTO.getPostText().isEmpty()) )
                throw new IllegalArgumentException("Post with TEXT type cannot have null post text");
            if (newPost.getPostType() == PostType.AUDIO && postDTO.getPostAudio() == null)
                throw new IllegalArgumentException("Post with AUDIO type cannot have null post audio");
            Language language = languageRepository.findById(postDTO.getLanguageId())
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find language with ID " + postDTO.getLanguageId()) );


            //Check valid file
            if (postDTO.getSubjectVideo() != null) checkFile(postDTO.getSubjectVideo(), "video");
            if (postDTO.getSubjectImg() != null) checkFile(postDTO.getSubjectImg(), "image");
            if (postDTO.getPostAudio() != null) checkFile(postDTO.getPostAudio(), "audio");

            //MultipartFile process
            CloudinaryResponse cloudinaryResponse = null;

            //Subject video
            if (postDTO.getSubjectVideo() != null) cloudinaryResponse = uploadFile(postDTO.getSubjectVideo());
            if (cloudinaryResponse != null) {
                newPost.setSubjectVideoUrl(cloudinaryResponse.getUrl());
                newPost.setSubjectVideoName(cloudinaryResponse.getPublicId());
            }

            //Subject image
            if (postDTO.getSubjectImg() != null) cloudinaryResponse = uploadFile(postDTO.getSubjectImg());
            if (cloudinaryResponse != null) {
                newPost.setSubjectImageUrl(cloudinaryResponse.getUrl());
                newPost.setSubjectImageName(cloudinaryResponse.getPublicId());
            }

            //Post audio
            if (postDTO.getPostAudio() != null) cloudinaryResponse = uploadFile(postDTO.getPostAudio());
            if (cloudinaryResponse != null) {
                newPost.setPostAudioUrl(cloudinaryResponse.getUrl());
                newPost.setPostAudioName(cloudinaryResponse.getPublicId());
            }

            //Get and set user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            newPost.setUser(user);
            newPost.setLanguage(language);

            //Save and map return
            newPost = postRepository.save(newPost);
            PostResponse postResponse = modelMapper.map(newPost, PostResponse.class);
            postResponse.setUserId(user.getId());
            postResponse.setLanguageId(language.getId());
            return postResponse;

        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create new post, err= "+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NEW_POST, requestId);
        }
    }

    @Override
    public Page<PostResponse> getPosts(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String postType, String language)
    {
        try {

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> posts = postRepository.findAll(PostSpecification.getSpecification(postType, language, sortBy, sortDirection, null), pageable);

            return posts.map(
                    post -> {

                        PostResponse postResponse = modelMapper.map(post, PostResponse.class);
                        postResponse.setUserId(post.getUser().getId());
                        postResponse.setLanguageId(post.getLanguage().getId());

                        return postResponse;

                    });

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get posts, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_POST, requestId);
        }
    }

    @Override
    public PostResponse getPost(String requestId, String postId)
    {
        try
        {

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Post with ID = " + postId));

            PostResponse postResponse = modelMapper.map(post, PostResponse.class);
            postResponse.setUserId(post.getUser().getId());
            postResponse.setLanguageId(post.getLanguage().getId());

            return postResponse;

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get post, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_POST, requestId);
        }
    }

    @Override
    public List<PostResponse> getByUserId(String requestId, String userId)
    {

        try {

            User existingUser = userRepository.findById(userId)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find user with ID " + userId ));

            List<Post> posts = postRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt"));

            return posts.stream().map(post ->
            {
                PostResponse postResponse = modelMapper.map(post, PostResponse.class);
                postResponse.setUserId(post.getUser().getId());
                postResponse.setLanguageId(post.getLanguage().getId());

                return postResponse;

            }).toList();

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get post list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_POST, requestId);
        }

    }

    public Page<PostResponse> getSelfPost(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String postType, String language)
    {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();

            Pageable pageable = PageRequest.of(page, size);

            Page<Post> posts = postRepository.findAll(PostSpecification.getSpecification(postType, language, sortBy, sortDirection, user.getId()), pageable);

            return posts.map(
                    post -> {

                        PostResponse postResponse = modelMapper.map(post, PostResponse.class);
                        postResponse.setUserId(post.getUser().getId());
                        postResponse.setLanguageId(post.getLanguage().getId());

                        return postResponse;

                    });

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get posts, err="+e.getMessage());
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
