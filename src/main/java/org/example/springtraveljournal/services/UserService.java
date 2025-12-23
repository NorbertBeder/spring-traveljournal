package org.example.springtraveljournal.services;

import org.example.springtraveljournal.models.dtos.request.LoginDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.LoginResponseDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.models.entities.User;

import java.util.List;

public interface UserService {

    User getUser(Long id);

    User createUser(UserRequestCreateDto userRequest);

    User updateUserAll(Long id, UserRequestCreateDto userRequest);

    User updateUserPartial(Long id, UserRequestUpdateDto userRequest);

    void deleteUser(Long id);

    List<User> getAllUsers();

    LoginResponseDto login(LoginDto login);
}
