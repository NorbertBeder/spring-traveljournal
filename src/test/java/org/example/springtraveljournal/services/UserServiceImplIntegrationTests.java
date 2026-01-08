package org.example.springtraveljournal.services;

import jakarta.transaction.Transactional;
import org.example.springtraveljournal.models.dtos.request.UserRequestCreateDto;
import org.example.springtraveljournal.models.dtos.request.UserRequestUpdateDto;
import org.example.springtraveljournal.models.dtos.response.UserResponseDto;
import org.example.springtraveljournal.models.entities.User;
import org.example.springtraveljournal.repositories.UserRepository;
import org.example.springtraveljournal.util.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceImplIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void cleanDb() {
        userRepository.deleteAll();
    }

    @Test
    void testCreateUser_returnUser_shouldWork() {
        UserRequestCreateDto userRequest = new UserRequestCreateDto();
        userRequest.setName("Jack");
        userRequest.setSurname("Smith");
        userRequest.setEmail("123@gmail.com");
        userRequest.setPassword("123");

        UserResponseDto userResponse = userService.createUser(userRequest);
        assertNotNull(userResponse);

        User savedUser = userRepository.findByEmail(userResponse.getEmail()).orElseThrow();
        UserResponseDto fetchedUser = userService.getUser(savedUser.getId());

        assertNotNull(fetchedUser);
    }

    @Test
    void testGetAllUsers_shouldReturnAll() {
        User u1 = new User();
        User u2 = new User();

        u1.setName("A");
        u1.setSurname("B");
        u1.setEmail("a@b.com");
        u1.setPassword("p");

        u2.setName("C");
        u2.setSurname("D");
        u2.setEmail("c@d.com");
        u2.setPassword("p");

        userRepository.saveAll(List.of(u1, u2));

        List<UserResponseDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void testUpdateUserPartial_shouldPersistChanges() {
        User u = new User();
        u.setName("Old");
        u.setSurname("User");
        u.setEmail("old@gmail.com");
        u.setPassword("p");
        u = userRepository.save(u);

        UserRequestUpdateDto userPatch = new UserRequestUpdateDto();
        userPatch.setName("New");

        userService.updateUserPartial(u.getId(), userPatch);

        User updatedUser = userRepository.findById(u.getId()).orElseThrow();
        assertEquals("New", updatedUser.getName());
        assertEquals("User", updatedUser.getSurname());
        assertEquals("p", updatedUser.getPassword());
        assertEquals("old@gmail.com", updatedUser.getEmail());
    }

    @Test
    void deleteUser_shouldRemoveFromDb() {
        User u = new User();
        u.setName("X");
        u.setSurname("Y");
        u.setEmail("x@y.com");
        u.setPassword("p");
        u = userRepository.save(u);

        userService.deleteUser(u.getId());

        assertFalse(userRepository.existsById(u.getId()));
    }
}
