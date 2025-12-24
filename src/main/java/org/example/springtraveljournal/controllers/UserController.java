package org.example.springtraveljournal.controllers;

import jakarta.validation.Valid;
import org.example.springtraveljournal.models.dtos.request.LoginDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.LoginResponseDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.services.UserService;
import org.example.springtraveljournal.util.mappers.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-journal/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping("/register")
    public UserResponseDto createUser(@RequestBody @Valid UserRequestCreateDto userRequest) {
        return userService.createUser(userRequest);
    }


    @PatchMapping("/{id}")
    public UserResponseDto updateUserPartial(@PathVariable Long id, @RequestBody @Valid UserRequestUpdateDto userRequest) {
        return userService.updateUserPartial(id, userRequest);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUserAll(@PathVariable Long id, @RequestBody @Valid UserRequestCreateDto userRequest) {
        return userService.updateUserAll(id, userRequest);
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
    }
}
