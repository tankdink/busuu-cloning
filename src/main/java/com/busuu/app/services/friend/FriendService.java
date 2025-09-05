package com.busuu.app.services.friend;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.FriendResponse;
import com.busuu.app.entities.Friend;
import com.busuu.app.entities.User;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.FriendRepository;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.specification.FriendSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService implements IFriendService
{

    private final FriendRepository friendRepository;

    private final UserRepository userRepository;


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
}
