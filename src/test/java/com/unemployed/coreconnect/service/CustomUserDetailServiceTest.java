package com.unemployed.coreconnect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.unemployed.coreconnect.factory.UserFactory;
import com.unemployed.coreconnect.model.entity.User;
import com.unemployed.coreconnect.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    public void testLoadUserByUsername_Success() {
        // Arrange
        User mockUser = UserFactory.create();
        mockUser.setUsername("testUser");
        mockUser.setPassword("testPassword");
        when(userRepository.findByUsernameOrEmail("testUser", "testUser")).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = customUserDetailService.loadUserByUsername("testUser");

        // Assert
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("testPassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsernameOrEmail("nonExistentUser", "nonExistentUser")).thenReturn(Optional.empty());

        // Act & Assert
        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailService.loadUserByUsername("nonExistentUser");
        });

        assertEquals("User not found with username or email: nonExistentUser", exception.getMessage());
    }
}
