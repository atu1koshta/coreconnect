package com.unemployed.coreconnect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.unemployed.coreconnect.exception.UserAlreadyExistsException;
import com.unemployed.coreconnect.factory.UserFactory;
import com.unemployed.coreconnect.model.entity.User;
import com.unemployed.coreconnect.repository.UserRepository;


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

        private User user;

        @BeforeEach
        public void init() {
            user = UserFactory.create();
        }

        @Test
        public void testSaveUser_Fail_UsernameOrEmailExists() {
            when(userRepository.existsByUsernameOrEmail(anyString(), anyString())).thenReturn(true);

            UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () -> {
                userService.saveUser(user);
            });

            assertEquals(exception.getMessage(), "Username or email already exists");
        }

        @Test
        public void testSaveUser_Success() {
            user = UserFactory.create();
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
