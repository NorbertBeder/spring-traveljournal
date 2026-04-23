package org.example.springtraveljournal.services.impl;

import org.example.springtraveljournal.models.dtos.response.FriendRequestResponseDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.models.entities.FriendRequest;
import org.example.springtraveljournal.models.entities.Friendship;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.models.enums.FriendRequestStatus;
import org.example.springtraveljournal.repositories.FriendRequestRepository;
import org.example.springtraveljournal.repositories.FriendshipRepository;
import org.example.springtraveljournal.services.FriendRequestService;
import org.example.springtraveljournal.services.UserService;
import org.example.springtraveljournal.util.SecurityUtil;
import org.example.springtraveljournal.util.exceptions.BadRequestException;
import org.example.springtraveljournal.util.mappers.FriendRequestMapper;
import org.example.springtraveljournal.util.mappers.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserService userService;
    private final FriendshipRepository friendshipRepository;
    private final UserMapper userMapper;

    public FriendRequestServiceImpl(FriendRequestRepository friendRequestRepository, UserService userService, FriendshipRepository friendshipRepository, UserMapper userMapper) {
        this.friendRequestRepository = friendRequestRepository;
        this.userService = userService;
        this.friendshipRepository = friendshipRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void sendFriendRequest(String receiverEmail) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userService.getUserEntity(userId);
        String senderEmail = user.getEmail();

        if (senderEmail.equals(receiverEmail)) {
            throw new BadRequestException("You cannot send a friend request to yourself.");
        }

        User sender = userService.getUserEntityByEmail(senderEmail);
        User receiver = userService.getUserEntityByEmail(receiverEmail);

        if (friendRequestRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId()).isPresent()) {
            throw new BadRequestException("Request already sent.");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequestStatus.PENDING);

        friendRequestRepository.save(friendRequest);
    }

    @Override
    public List<FriendRequestResponseDto> getPendingFriendRequests() {
        Long userId = SecurityUtil.getCurrentUserId();

        return friendRequestRepository.findByReceiverIdAndStatus(userId, FriendRequestStatus.PENDING)
                .stream()
                .map(FriendRequestMapper::friendRequestToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Long requestId) {
        Long userId = SecurityUtil.getCurrentUserId();

        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BadRequestException("Friend request not found."));

        User receiver = friendRequest.getReceiver();

        if (!receiver.getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to accept this friend request.");
        }
        User sender = friendRequest.getSender();

        friendshipRepository.save(new Friendship(receiver, sender));
        friendshipRepository.save(new Friendship(sender, receiver));

        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public void declineFriendRequest(Long requestId) {
        Long userId = SecurityUtil.getCurrentUserId();

        FriendRequest friendRequest = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new BadRequestException("Friend request not found."));

        if (!friendRequest.getReceiver().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to decline this friend request.");
        }

        friendRequest.setStatus(FriendRequestStatus.DECLINED);
        friendRequestRepository.save(friendRequest);
    }

    @Override
    public List<UserResponseDto> getFriendsList() {
        Long userId = SecurityUtil.getCurrentUserId();

        return friendshipRepository.findByUserId(userId)
                .stream()
                .map(Friendship::getFriend)
                .map(userMapper::userToUserResponseDto)
                .toList();
    }

    @Override
    public List<Long> getFriendIds(Long userId) {
        return friendshipRepository.findByUserId(userId)
                .stream()
                .map(f -> f.getFriend().getId())
                .toList();
    }

    @Override
    public void removeFriend(Long friendId) {
        Long userId = SecurityUtil.getCurrentUserId();

        friendshipRepository.deleteByUserIdAndFriendId(friendId, userId);
        friendshipRepository.deleteByUserIdAndFriendId(userId, friendId);
    }
}
