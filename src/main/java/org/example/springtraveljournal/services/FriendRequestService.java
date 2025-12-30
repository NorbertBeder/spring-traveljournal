package org.example.springtraveljournal.services;

import org.example.springtraveljournal.models.dtos.response.FriendRequestResponseDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;

import java.util.List;

public interface FriendRequestService {

    void sendFriendRequest(String receiverEmail);

    List<FriendRequestResponseDto> getPendingFriendRequests();

    void acceptFriendRequest(Long requestId);

    void declineFriendRequest(Long requestId);

    List<UserResponseDto> getFriendsList();

    void removeFriend(Long friendId);

    List<Long> getFriendIds(Long userId);
}
