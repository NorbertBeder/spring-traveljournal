package org.example.springtraveljournal.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.springtraveljournal.models.dtos.request.LoginDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.LoginResponseDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.services.UserService;
import org.example.springtraveljournal.util.exceptions.BadRequestException;
import org.example.springtraveljournal.util.exceptions.ResourceNotFoundException;
import org.example.springtraveljournal.util.jwt.JwtFilter;
import org.example.springtraveljournal.util.jwt.JwtUtil;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureJsonTesters
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    UserService userService;
    @MockitoBean
    JwtUtil jwtUtil;
    @MockitoBean
    JwtFilter jwtFilter;

    @Test
    void TestGetUser_Success_returnUserResponseJson() throws Exception {
        UserResponseDto user = new UserResponseDto();
        user.setId(1L);
        user.setName("User");
        user.setEmail("123@gmail.com");
        user.setSurname("Surname");

        when(userService.getUser(1L)).thenReturn(user);

        mockMvc.perform(get("/travel-journal/users/1")).andExpect(status().isOk());

        verify(userService).getUser(1L);
    }

    @Test
    void TestGetUser_NotFound() throws Exception {
        when(userService.getUser(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/travel-journal/users/1"))
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).getUser(1L);
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(new UserResponseDto(), new UserResponseDto()));

        mockMvc.perform(get("/travel-journal/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.length()").value(2));

        verify(userService).getAllUsers();
    }

    @Test
    void testRegister_Success() throws Exception {
        UserRequestCreateDto req = new UserRequestCreateDto();
        req.setName("User");
        req.setSurname("Surname");
        req.setPassword("123");
        req.setEmail("123@gmail.com");

        when(userService.createUser(any(UserRequestCreateDto.class))).thenReturn(new UserResponseDto());

        mockMvc.perform(post("/travel-journal/users/register")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(userService).createUser(any(UserRequestCreateDto.class));
    }

    @Test
    void testRegister_BadRequest() throws Exception {
        UserRequestCreateDto req = new UserRequestCreateDto();
        req.setName("User");
        req.setSurname("Surname");
        req.setPassword("123");
        req.setEmail("123@gmail.com");

        when(userService.createUser(any(UserRequestCreateDto.class))).thenThrow(new BadRequestException("Email already registered"));

        mockMvc.perform(post("/travel-journal/users/register")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Email already registered"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).createUser(any(UserRequestCreateDto.class));
    }

    @Test
    void testUpdateUserPartial_Success() throws Exception {
        UserRequestCreateDto req = new UserRequestCreateDto();
        req.setName("newUser");

        when(userService.updateUserPartial(eq(1L), any(UserRequestUpdateDto.class)))
                .thenReturn(new UserResponseDto());

        mockMvc.perform(patch("/travel-journal/users/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(userService).updateUserPartial(eq(1L), any(UserRequestUpdateDto.class));
    }

    @Test
    void testUpdateUserPartial_NotFound() throws Exception {
        UserRequestUpdateDto req = new UserRequestUpdateDto();
        req.setName("newUser");

        when(userService.updateUserPartial(eq(1L), any(UserRequestUpdateDto.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(patch("/travel-journal/users/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).updateUserPartial(eq(1L), any(UserRequestUpdateDto.class));
    }

    @Test
    void testUpdateUserAll_Success() throws Exception {
        UserRequestCreateDto req = new UserRequestCreateDto();
        req.setName("John");
        req.setSurname("Doe");
        req.setEmail("new@gmail.com");
        req.setPassword("newPassword");

        when(userService.updateUserAll(eq(1L), any(UserRequestCreateDto.class)))
                .thenReturn(new UserResponseDto());

        mockMvc.perform(put("/travel-journal/users/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(userService).updateUserAll(eq(1L), any(UserRequestCreateDto.class));
    }

    @Test
    void testUpdateUserAll_BadRequest() throws Exception {
        UserRequestCreateDto req = new UserRequestCreateDto();
        req.setName("John");
        req.setSurname("Doe");
        req.setEmail("new@gmail.com");
        req.setPassword("newPassword");

        when(userService.updateUserAll(eq(1L), any(UserRequestCreateDto.class)))
                .thenThrow(new BadRequestException("No valid changes provided"));

        mockMvc.perform(put("/travel-journal/users/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("No valid changes provided"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).updateUserAll(eq(1L), any(UserRequestCreateDto.class));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/travel-journal/users/1")).andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found")).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/travel-journal/users/1"))
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).deleteUser(1L);
    }

    @Test
    void login_Success_returnToken() throws Exception {
        LoginDto req = new LoginDto();
        req.setEmail("123@gmail.com");
        req.setPassword("123");

        LoginResponseDto res = new LoginResponseDto();
        res.setToken("token");
        res.setUser(new UserResponseDto());

        when(userService.login(any(LoginDto.class))).thenReturn(res);

        mockMvc.perform(post("/travel-journal/users/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.token").value("token"));

        verify(userService).login(any(LoginDto.class));
    }

    @Test
    void testLogin_BadRequest_whenInvalidPassword() throws Exception {
        LoginDto req = new LoginDto();
        req.setEmail("123@gmail.com");
        req.setPassword("wrong");

        when(userService.login(any(LoginDto.class)))
                .thenThrow(new BadRequestException("Invalid password"));

        mockMvc.perform(post("/travel-journal/users/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid password"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).login(any(LoginDto.class));
    }

    @Test
    void testLogin_notFound_whenUserNotRegistered() throws Exception {
        LoginDto req = new LoginDto();
        req.setEmail("missing@doe.com");
        req.setPassword("x");

        when(userService.login(any(LoginDto.class)))
                .thenThrow(new ResourceNotFoundException("User not registered"));

        mockMvc.perform(post("/travel-journal/users/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(content().contentTypeCompatibleWith(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not registered"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService).login(any(LoginDto.class));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/travel-journal/users/logout"))
                .andExpect(status().isNoContent());

        verifyNoInteractions(userService);
    }
}
