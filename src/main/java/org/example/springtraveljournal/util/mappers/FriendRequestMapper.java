package org.example.springtraveljournal.util.mappers;

import org.example.springtraveljournal.models.dtos.response.FriendRequestResponseDto;
import org.example.springtraveljournal.models.entities.FriendRequest;

public class FriendRequestMapper {
    public static FriendRequestResponseDto friendRequestToResponseDto(FriendRequest request) {
        FriendRequestResponseDto dto = new FriendRequestResponseDto();
        dto.setId(request.getId());
        dto.setSenderEmail(request.getSender().getEmail());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
    }
}
