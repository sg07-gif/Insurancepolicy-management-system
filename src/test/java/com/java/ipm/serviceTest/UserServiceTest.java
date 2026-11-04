package com.java.ipm.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.java.ipm.dto.AuthRequest;
import com.java.ipm.dto.AuthResponse;
import com.java.ipm.entity.User;
import com.java.ipm.repository.UserRepository;
import com.java.ipm.security.JwtUtil;
import com.java.ipm.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("john_doe");
        testUser.setPassword("password");
        testUser.setEmail("john@example.com");
        testUser.setRole(com.java.ipm.domain.Role.USER);

        authRequest = new AuthRequest();
        authRequest.setUsername("john_doe");
        authRequest.setPassword("password");
        authRequest.setEmail("john@example.com");
    }

    // Test successful registration
    @Test
    void register_Success() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(authRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String response = userService.register(authRequest);
        assertEquals("User registered successfully", response);
    }

    @Test
    void register_AlwaysAssignsUserRole() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(authRequest.getPassword())).thenReturn("encodedPassword");

        userService.register(authRequest);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals(com.java.ipm.domain.Role.USER, saved.getValue().getRole());
        assertEquals("encodedPassword", saved.getValue().getPassword());
    }

    @Test
    void register_Failure_EmailMissing() {
        authRequest.setEmail(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.register(authRequest));
        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    void register_Failure_UsernameExists() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(RuntimeException.class, () -> userService.register(authRequest));
        assertEquals("Username already exists", exception.getMessage());
    }

    // Test successful login
    @Test
    void login_Success() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(testUser)).thenReturn("mocked-jwt-token");

        AuthResponse response = userService.login(authRequest);
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
    }

    @Test
    void login_Failure_UserNotFound() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.login(authRequest));
        assertEquals("User not found", exception.getMessage());
    }

    // Test get all users
    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser, new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> response = userService.getAllUsers();
        assertEquals(2, response.size());
    }
}
