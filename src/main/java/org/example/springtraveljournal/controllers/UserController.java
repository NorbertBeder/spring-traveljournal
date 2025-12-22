package org.example.springtraveljournal.controllers;

import jakarta.validation.Valid;
import org.example.springtraveljournal.models.dtos.request.LoginDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.services.UserService;
import org.example.springtraveljournal.util.UserMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("travel-journal/")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/user")
    public UserResponseDto createUser(@RequestBody @Valid UserRequestCreateDto userRequest) {
        User user = userService.createUser(userRequest);
        return userMapper.userToUserResponseDto(user);
    }

    @GetMapping("/user/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return userMapper.userToUserResponseDto(user);
    }

    @PutMapping("/user/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @RequestBody @Valid UserRequestUpdateDto userRequest) {
        User user = userService.updateUser(id, userRequest);
        return userMapper.userToUserResponseDto(user);
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.stream()
                .map(userMapper::userToUserResponseDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/login")
    public UserResponseDto login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }
}
