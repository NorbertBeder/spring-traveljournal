package org.example.springtraveljournal.services.impl;


import org.example.springtraveljournal.models.dtos.request.LoginDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.LoginResponseDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.repositories.UserRepository;
import org.example.springtraveljournal.services.UserService;
import org.example.springtraveljournal.util.exceptions.BadRequestException;
import org.example.springtraveljournal.util.exceptions.ResourceNotFoundException;
import org.example.springtraveljournal.util.jwt.JwtUtil;
import org.example.springtraveljournal.util.mappers.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserResponseDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.userToUserResponseDto(user);
    }

    @Override
    public UserResponseDto createUser(UserRequestCreateDto userRequest) {
        if (userRequest.getName() == null || userRequest.getSurname() == null || userRequest.getEmail() == null) {
            throw new BadRequestException("Invalid user data");
        }

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }
        User user = userRepository.save(userMapper.userRequestCreateDto(userRequest));

        return userMapper.userToUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateUserAll(Long id, UserRequestCreateDto userRequest) {
        String name = userRequest.getName();
        String surname = userRequest.getSurname();
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();

        if (id == null || name == null || surname == null || email == null || password == null) {
            throw new BadRequestException("No valid changes provided");
        }

        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingUser.setName(name);
        existingUser.setSurname(surname);
        existingUser.setPassword(passwordEncoder.encode(password));
        existingUser.setEmail(email);
        userRepository.save(existingUser);

        return userMapper.userToUserResponseDto(existingUser);
    }

    @Override
    public UserResponseDto updateUserPartial(Long id, UserRequestUpdateDto userRequest) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userRequest.getName() != null) {
            existingUser.setName(userRequest.getName());
        }

        if (userRequest.getSurname() != null) {
            existingUser.setSurname(userRequest.getSurname());
        }

        if (userRequest.getEmail() != null) {
            existingUser.setEmail(userRequest.getEmail());
        }
        userRepository.save(existingUser);

        return userMapper.userToUserResponseDto(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::userToUserResponseDto).toList();
    }

    @Override
    public LoginResponseDto login(LoginDto login) {
        User user = userRepository.findByEmail(login.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not registered"));

        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }
        UserResponseDto userResponse = userMapper.userToUserResponseDto(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        LoginResponseDto loginResponse = new LoginResponseDto();

        loginResponse.setToken(token);
        loginResponse.setUser(userResponse);

        return loginResponse;
    }
}
