package org.example.springtraveljournal.services.impl;


import org.example.springtraveljournal.models.dtos.request.LoginDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.repositories.UserRepository;
import org.example.springtraveljournal.services.UserService;
import org.example.springtraveljournal.util.UserMapper;
import org.example.springtraveljournal.util.exceptions.BadRequestException;
import org.example.springtraveljournal.util.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User createUser(UserRequestCreateDto userRequest) {
        if(userRequest.getName() == null || userRequest.getSurname() == null || userRequest.getEmail() == null) {
            throw new BadRequestException("Invalid user data");
        }

        if(userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new BadRequestException("Email already in use");
        }

        User user = userMapper.userResponseDtoToUser(userRequest);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserRequestUpdateDto userRequest) {

        String name = userRequest.getName();
        String surname = userRequest.getSurname();

        boolean nameExists = StringUtils.hasText(name);
        boolean surnameExists = StringUtils.hasText(surname);

        if(id == null || (!nameExists && !surnameExists)) {
            throw new BadRequestException("No valid changes provided");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(nameExists && !name.equals(existingUser.getName())) {
            existingUser.setName(name);
        }

        if(surnameExists && !surname.equals(existingUser.getSurname())) {
            existingUser.setSurname(surname);
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponseDto login(LoginDto login) {

        User user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not registered"));

        if(!user.getPassword().equals(login.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        return userMapper.userToUserResponseDto(user);
    }
}
