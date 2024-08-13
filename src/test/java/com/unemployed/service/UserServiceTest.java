package com.unemployed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.unemployed.coreconnect.model.entity.User;
import com.unemployed.coreconnect.repository.UserRepository;
import com.unemployed.coreconnect.service.UserService;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class SaveUserTests {

        @Test
        public void testSaveUser_Success() {
            User user = new User();
            user.setUsername("john");
            user.setEmail("john.doe@example.com");
            user.setPassword("password123");

            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            User savedUser = userService.saveUser(user);

            assertNotNull(savedUser);
            assertEquals("john", savedUser.getUsername());
            assertEquals("john.doe@example.com", savedUser.getEmail());
            assertEquals("encodedPassword", savedUser.getPassword());
        }
    }

}
