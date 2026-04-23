package org.example.springtraveljournal.controllers;

import jakarta.validation.Valid;
import org.example.springtraveljournal.models.dtos.request.FriendRequestCreateDto;
import org.example.springtraveljournal.models.dtos.response.FriendRequestResponseDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.services.FriendRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("travel-journal/friends")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @PostMapping
    public List<UserResponseDto> myFriends() {
        return friendRequestService.getFriendsList();
    }

    @PostMapping("/request")
    public void sendFriendRequest(@Valid @RequestBody FriendRequestCreateDto friendRequestCreateDto) {
        friendRequestService.sendFriendRequest(friendRequestCreateDto.getReceiverEmail());
    }

    @GetMapping("/requests")
    public List<FriendRequestResponseDto> pendingFriendRequests() {
        return friendRequestService.getPendingFriendRequests();
    }

    @PostMapping("/requests/{requestId}/accept")
    public void acceptFriendRequest(@PathVariable Long requestId) {
        friendRequestService.acceptFriendRequest(requestId);
    }

    @PostMapping("/requests/{requestId}/decline")
    public void declineFriendRequest(@PathVariable Long requestId) {
        friendRequestService.declineFriendRequest(requestId);
    }


}
