package org.example.springtraveljournal.services;

import org.example.springtraveljournal.models.dtos.request.LoginDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.LoginResponseDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.models.entities.User;

import java.util.List;

public interface UserService {

    UserResponseDto getUser(Long id);

    User getUserEntity(Long id);

    User getUserEntityByEmail(String email);

    UserResponseDto createUser(UserRequestCreateDto userRequest);

    UserResponseDto updateUserAll(Long id, UserRequestCreateDto userRequest);

    UserResponseDto updateUserPartial(Long id, UserRequestUpdateDto userRequest);

    void deleteUser(Long id);

    List<UserResponseDto> getAllUsers();

    LoginResponseDto login(LoginDto login);
}
