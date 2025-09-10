package com.busuu.app.services.friend;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.FriendResponse;
import com.busuu.app.dtos.responses.UserLanguageResponse;
import com.busuu.app.entities.Friend;
import com.busuu.app.entities.User;
import com.busuu.app.entities.UserLanguage;
import com.busuu.app.entities.enums.LearningStatus;
import com.busuu.app.entities.enums.SpeakingStatus;
import com.busuu.app.entities.posts.Post;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.FriendRepository;
import com.busuu.app.repositories.UserLanguageRepository;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.specification.FriendSpecification;
import com.busuu.app.specification.UserLanguageSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService implements IFriendService
{

    private final FriendRepository friendRepository;

    private final UserRepository userRepository;

    private final UserLanguageRepository userLanguageRepository;

    private final ModelMapper modelMapper;


    @Override
    public void addFriend(String requestId, String userId)
    {

        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();

            //Check valid userId, already friend, add friend to self
            User existingUser = userRepository.findById(userId)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find user with ID " + userId ));

            boolean exists = friendRepository.existsByUserAndFriend(user, existingUser);
            if (exists) {
                throw new ExistDataException("You and that user is already friend!");
            }
            boolean existsReversed = friendRepository.existsByFriendAndUser(existingUser, user);
            if (existsReversed) {
                throw new ExistDataException("That user and you is already friend!");
            }

            if (user.getId().equals(existingUser.getId())) throw new IllegalArgumentException("Cannot add friend to yourself!");



            //Adding relationship
            Friend newFriend = Friend.builder()
                    .id(UUID.randomUUID().toString())
                    .user(user)
                    .friend(existingUser)
                    .build();

            Friend reversed = Friend.builder()
                    .id(UUID.randomUUID().toString())
                    .user(existingUser)
                    .friend(user)
                    .build();


            friendRepository.save(newFriend);
            friendRepository.save(reversed);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to add friend, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_ADD_FRIEND, requestId);
        }


    }

    @Override
    public FriendResponse getFriends(String requestId, List<String> sortBy, List<String> sortDirection, String searchValue, String country)
    {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            List<Friend> friendList = friendRepository.findAll(FriendSpecification.getSpecification(userId, searchValue, sortBy, sortDirection, country));

            List<String> friendIds = friendList.stream().map(
                    friend -> friend.getFriend().getId()
            ).toList();

            FriendResponse response = FriendResponse.builder()
                    .userId(userId)
                    .friendIds(friendIds)
                    .build();

            return response;


        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get friend list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LESSON, requestId);
        }
    }

    @Override
    public FriendResponse getFriendsByUserId(String userId)
    {
        try {

            List<Friend> friendList = friendRepository.findByUserId(userId);

            List<String> friendIds = friendList.stream().map(
                    friend -> friend.getFriend().getId()
            ).toList();

            FriendResponse response = FriendResponse.builder()
                    .userId(userId)
                    .friendIds(friendIds)
                    .build();

            return response;


        } catch (Exception e) {
            log.error("request failed to get friend list, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LESSON, "Internal request" );
        }
    }

    @Override
    public List<String> getRandomList(String requestId)
    {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();

            //Get speaking and learning language of self
            List<String> learning = new ArrayList<>();
            List<String> speaking = new ArrayList<>();

            List<UserLanguage> userLanguages = userLanguageRepository.findByUserId(user.getId());

            for (UserLanguage userLanguage : userLanguages)
            {

                if (userLanguage.getLearningStatus().equals(LearningStatus.IN_PROGRESS)) learning.add(userLanguage.getLanguage().getName());
                if (!userLanguage.getSpeakingStatus().equals(SpeakingStatus.NO_PROFICIENCY)) speaking.add(userLanguage.getLanguage().getName());

            }


            Specification<UserLanguage> spec = UserLanguageSpecification.getSpecification(learning, speaking);
            List<UserLanguageResponse> responseList = userLanguageRepository.findAll(spec).stream().map(
                    userLanguage ->  {

                        UserLanguageResponse res = modelMapper.map(userLanguage, UserLanguageResponse.class);
                        res.setLanguageId(userLanguage.getLanguage().getId());
                        res.setUserId(userLanguage.getUser().getId());

                        return res;
                    }
            ).toList(); //This list is unmodifiable

            //Get user id from list
            List<String> userIdList = new ArrayList<>();
            for (UserLanguageResponse userLanguage : responseList) userIdList.add(userLanguage.getUserId());

            //Remove duplicate
            Set<String> set = new HashSet<>(userIdList);
            List<String> response = new ArrayList<>(set);
            response.remove(user.getId());

            return randomize(response);

        } catch (Exception e) {
            log.error("requestId=" + requestId + ",failed to get topics by type, err=" + e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_TOPIC, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteFriend(String requestId, String userId)
    {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();

            //Check valid userId, not friend, delete to self
            User existingUser = userRepository.findById(userId)
                    .orElseThrow( ()-> new DataNotFoundException("Cannot find user with ID " + userId ));

            boolean exists = friendRepository.existsByUserAndFriend(user, existingUser);
            if (!exists) {
                throw new DataNotFoundException("You and that user is not friend!");
            }
            boolean existsReversed = friendRepository.existsByFriendAndUser(existingUser, user);
            if (!existsReversed) {
                throw new ExistDataException("That user and you is not friend!");
            }

            if (user.getId().equals(existingUser.getId())) throw new IllegalArgumentException("Cannot process to yourself!");

            friendRepository.deleteByUserIdAndFriendId(user.getId(), userId);
            friendRepository.deleteByUserIdAndFriendId(userId, user.getId());

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete course, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_COURSE, requestId);
        }
    }

    public List<String> randomize(List<String> list)
    {

        if (list.size() <= 5) {
            return list;
        }

        //Response list
        List<String> resultList = new ArrayList<>(5);

        //Shuffle the copy list
        List<String> copy = new ArrayList<>(list);
        Collections.shuffle(copy);

        //Add the first 5 elements of the shuffled list to result list
        for (int i = 0; i < 5; i++) resultList.add(copy.get(i));

        return resultList;

    }
}
