package org.example.springtraveljournal.services;

import org.example.springtraveljournal.models.dtos.request.LoginDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.LoginResponseDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.repositories.UserRepository;
import org.example.springtraveljournal.services.impl.UserServiceImpl;
import org.example.springtraveljournal.util.exceptions.BadRequestException;
import org.example.springtraveljournal.util.exceptions.ResourceNotFoundException;
import org.example.springtraveljournal.util.jwt.JwtUtil;
import org.example.springtraveljournal.util.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTests {

    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    UserServiceImpl userService;

    private User user;
    private UserResponseDto userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("123@gmail.com");
        user.setPassword("123");

        userResponse = new UserResponseDto();
        userResponse.setId(1L);
        userResponse.setName("John");
        userResponse.setSurname("Doe");
        userResponse.setEmail("123@gmail.com");
    }

    @Test
    void testGetUser_Success_ReturnsUserResponseDto() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponse);

        UserResponseDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).userToUserResponseDto(user);
    }

    @Test
    void testGetUser_UserNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        try {
            userService.getUser(userId);
        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
        }

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(0)).userToUserResponseDto(any());
    }

    @Test
    void testGetUserEntity_Success_ReturnsUserEntity() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserEntity(userId);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserEntity_UserNotFound_ThrowsResourceNotFoundException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        try {
            userService.getUserEntity(userId);
        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
        }

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserEntityByEmail_Success_ReturnsUserEntity() {
        String email = "123@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserEntityByEmail(email);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testGetUserEntityByEmail_UserNotFound_ThrowsResourceNotFoundException() {
        String email = "missing@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        try {
            userService.getUserEntityByEmail(email);
        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
        }

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testCreateUser_Success_ReturnsUserResponseDtoAndSavesInDb_whenValidAndEmailNotRegistered() {
        UserRequestCreateDto userRequest = mock(UserRequestCreateDto.class);
        when(userRequest.getName()).thenReturn("John");
        when(userRequest.getSurname()).thenReturn("Doe");
        when(userRequest.getEmail()).thenReturn("123@gmail.com");

        User mappedUser = new User();
        when(userRepository.findByEmail("123@gmail.com")).thenReturn(Optional.empty());
        when(userMapper.userRequestCreateDto(userRequest)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(user);
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponse);

        UserResponseDto result = userService.createUser(userRequest);
        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository, times(1)).findByEmail("123@gmail.com");
        verify(userMapper, times(1)).userRequestCreateDto(userRequest);
        verify(userRepository, times(1)).save(mappedUser);
        verify(userMapper, times(1)).userToUserResponseDto(user);
    }

    @Test
    void testCreateUser_BadRequest_whenEmailAlreadyRegistered() {
        UserRequestCreateDto userRequest = mock(UserRequestCreateDto.class);
        when(userRequest.getName()).thenReturn("John");
        when(userRequest.getSurname()).thenReturn("Doe");
        when(userRequest.getEmail()).thenReturn("123@gmail.com");

        when(userRepository.findByEmail("123@gmail.com")).thenReturn(Optional.of(user));

        try {
            userService.createUser(userRequest);
        } catch (Exception e) {
            assertEquals("Email already registered", e.getMessage());
        }

        verify(userRepository, times(1)).findByEmail("123@gmail.com");
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponseDto(any(User.class));
    }

    @Test
    void testCreateUser_BadRequest_whenNameNull() {
        UserRequestCreateDto userRequest = mock(UserRequestCreateDto.class);
        when(userRequest.getName()).thenReturn(null);

        try {
            userService.createUser(userRequest);
        } catch (Exception e) {
            assertEquals("Invalid user data", e.getMessage());
        }

        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void testCreateUser_BadRequest_whenSurnameNull() {
        UserRequestCreateDto userRequest = mock(UserRequestCreateDto.class);
        when(userRequest.getName()).thenReturn("John");
        when(userRequest.getSurname()).thenReturn(null);

        try {
            userService.createUser(userRequest);
        } catch (Exception e) {
            assertEquals("Invalid user data", e.getMessage());
        }

        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void testCreateUser_BadRequest_whenEmailNull() {
        UserRequestCreateDto userRequest = mock(UserRequestCreateDto.class);
        when(userRequest.getName()).thenReturn("John");
        when(userRequest.getSurname()).thenReturn("Doe");
        when(userRequest.getEmail()).thenReturn(null);

        try {
            userService.createUser(userRequest);
        } catch (Exception e) {
            assertEquals("Invalid user data", e.getMessage());
        }

        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void testUpdateUserAll_Success_ReturnsUpdatedUserResponseDto_whenAllValid() {
        Long userId = 1L;

        UserRequestCreateDto userRequest = mock(UserRequestCreateDto.class);
        when(userRequest.getName()).thenReturn("Jane");
        when(userRequest.getSurname()).thenReturn("Smith");
        when(userRequest.getEmail()).thenReturn("newMail@gmail.com");
        when(userRequest.getPassword()).thenReturn("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponse);

        UserResponseDto result = userService.updateUserAll(userId, userRequest);

        assertNotNull(result);
        assertSame(userResponse, result);
        assertEquals(userResponse, result);

        verify(userRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).userToUserResponseDto(user);
    }

    @Test
    void testUpdateUserAll_BadRequest_whenAnyFieldNull() {
        Long userId = 1L;

        UserRequestCreateDto userRequest = mock(UserRequestCreateDto.class);
        when(userRequest.getName()).thenReturn("Jane");
        when(userRequest.getSurname()).thenReturn("Smith");
        when(userRequest.getEmail()).thenReturn(null);
        when(userRequest.getPassword()).thenReturn("123");

        try {
            userService.updateUserAll(userId, userRequest);
        } catch (Exception e) {
            assertEquals("No valid changes provided", e.getMessage());
        }

        verify(userRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponseDto(any(User.class));
    }

    @Test
    void testUpdateUserAll_BadRequest_whenIdNull() {
        UserRequestCreateDto userRequest = mock(UserRequestCreateDto.class);

        when(userRequest.getName()).thenReturn("Jane");
        when(userRequest.getSurname()).thenReturn("Smith");
        when(userRequest.getEmail()).thenReturn("123@gmail.com");
        when(userRequest.getPassword()).thenReturn("123");

        assertThrows(BadRequestException.class, () -> userService.updateUserAll(null, userRequest));
        verifyNoInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void testUpdateUserAll_NotFound_whenUserMissing() {
        Long userId = 1L;
        UserRequestCreateDto userRequest = mock(UserRequestCreateDto.class);

        when(userRequest.getName()).thenReturn("Jane");
        when(userRequest.getSurname()).thenReturn("Smith");
        when(userRequest.getEmail()).thenReturn("new@gmail.com");
        when(userRequest.getPassword()).thenReturn("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserAll(userId, userRequest));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponseDto(any(User.class));
    }

    @Test
    void testUpdatePartial_UpdateAllFields_whenALlProvided() {
        Long userId = 1L;
        UserRequestUpdateDto userRequest = mock(UserRequestUpdateDto.class);

        when(userRequest.getName()).thenReturn("Jane");
        when(userRequest.getSurname()).thenReturn("Smith");
        when(userRequest.getEmail()).thenReturn("new@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponse);

        UserResponseDto result = userService.updateUserPartial(userId, userRequest);

        assertSame(userResponse, result);
        assertEquals(userResponse, result);

        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).userToUserResponseDto(user);
    }

    @Test
    void updateUserPartial_UpdateOnlyName_whenOnlyNameProvided() {
        Long userId = 1L;

        UserRequestUpdateDto dto = mock(UserRequestUpdateDto.class);
        when(dto.getName()).thenReturn("OnlyName");
        when(dto.getSurname()).thenReturn(null);
        when(dto.getEmail()).thenReturn(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponse);

        UserResponseDto result = userService.updateUserPartial(userId, dto);

        assertSame(userResponse, result);
        assertEquals("OnlyName", user.getName());
        assertEquals("Doe", user.getSurname());
        assertEquals("123@gmail.com", user.getEmail());

        verify(userRepository).save(user);
        verify(userMapper).userToUserResponseDto(user);
    }

    @Test
    void updateUserPartial_UpdateOnlySurname_whenOnlySurnameProvided() {
        Long userId = 1L;

        UserRequestUpdateDto dto = mock(UserRequestUpdateDto.class);
        when(dto.getName()).thenReturn(null);
        when(dto.getSurname()).thenReturn("OnlySurname");
        when(dto.getEmail()).thenReturn(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponse);

        UserResponseDto result = userService.updateUserPartial(userId, dto);

        assertSame(userResponse, result);
        assertEquals("John", user.getName());
        assertEquals("OnlySurname", user.getSurname());
        assertEquals("123@gmail.com", user.getEmail());

        verify(userRepository).save(user);
        verify(userMapper).userToUserResponseDto(user);
    }

    @Test
    void updateUserPartial_UpdateOnlyEmail_whenOnlyEmailProvided() {
        Long userId = 1L;

        UserRequestUpdateDto dto = mock(UserRequestUpdateDto.class);
        when(dto.getName()).thenReturn(null);
        when(dto.getSurname()).thenReturn(null);
        when(dto.getEmail()).thenReturn("new@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponse);

        UserResponseDto result = userService.updateUserPartial(userId, dto);

        assertSame(userResponse, result);
        assertEquals("John", user.getName());
        assertEquals("Doe", user.getSurname());
        assertEquals("new@gmail.com", user.getEmail());

        verify(userRepository).save(user);
        verify(userMapper).userToUserResponseDto(user);
    }

    @Test
    void updateUserPartial_NotFound_whenUserMissing() {
        Long userId = 1L;

        UserRequestUpdateDto dto = mock(UserRequestUpdateDto.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserPartial(userId, dto));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    void testDeleteUser_Success_whenUserExists() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUser_NotFound_whenUserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void getAllUsers_Success_returnMappedUsersToDtos() {
        User user1 = new User();
        User user2 = new User();

        user1.setId(1L);
        user2.setId(2L);

        UserResponseDto userResponse1 = new UserResponseDto();
        UserResponseDto userResponse2 = new UserResponseDto();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.userToUserResponseDto(user1)).thenReturn(userResponse1);
        when(userMapper.userToUserResponseDto(user2)).thenReturn(userResponse2);

        List<UserResponseDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertSame(userResponse1, result.getFirst());
        assertSame(userResponse2, result.getLast());

        verify(userRepository, times(1)).findAll();
        verify(userMapper).userToUserResponseDto(user1);
        verify(userMapper).userToUserResponseDto(user2);
    }

    @Test
    void testLogin_notFound_whenUserNotRegistered() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("missing@gmail.com");
        loginDto.setPassword("password");

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.login(loginDto));
        verify(userRepository).findByEmail(loginDto.getEmail());
        verifyNoInteractions(passwordEncoder, jwtUtil, userMapper);
    }

    @Test
    void testLogin_Success_returnTokenAndUser() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("123@gmail.com");
        loginDto.setPassword("password");

        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "123")).thenReturn(true);
        when(userMapper.userToUserResponseDto(user)).thenReturn(userResponse);
        when(jwtUtil.generateToken(1L, loginDto.getEmail())).thenReturn("token");

        LoginResponseDto result = userService.login(loginDto);

        assertNotNull(result);
        assertEquals("token", result.getToken());
        assertSame(userResponse, result.getUser());

        verify(userRepository).findByEmail(loginDto.getEmail());
        verify(passwordEncoder).matches("password", "123");
        verify(userMapper).userToUserResponseDto(user);
        verify(jwtUtil).generateToken(1L, loginDto.getEmail());
    }
}
