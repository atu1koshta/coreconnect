package com.unemployed.coreconnect.model;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.unemployed.coreconnect.factory.UserFactory;
import com.unemployed.coreconnect.model.entity.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UserTest {
    private Validator validator;
    private User user;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        user = UserFactory.create();
    }

    @Nested
    class ValidationTests {
        @Test
        public void testUsernameNotNull() {
            user.setUsername(null);
            user.setEmail("test@example.com");
            user.setPassword("password");

            Set<ConstraintViolation<User>> violations = validator.validate(user);

            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username cannot be empty")));
        }

        @Test
        public void testUsernameNotBlank() {
            user.setUsername("");
            user.setEmail("test@example.com");
            user.setPassword("password");

            Set<ConstraintViolation<User>> violations = validator.validate(user);

            for (ConstraintViolation<User> violation : violations) {
                System.out.println(violation.getMessage());
            }

            assertEquals(2, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Username cannot be empty")));
        }

        @Test
        public void testUsernameSize() {
            user.setUsername("abc");
            user.setEmail("test@example.com");
            user.setPassword("password");

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("size must be between 4 and 12")));
        }

        @Test
        public void testEmailNotNull() {
            user.setUsername("testuser");
            user.setEmail(null);
            user.setPassword("password");

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email cannot be empty")));
        }

        @Test
        public void testEmailNotBlank() {
            user.setUsername("testuser");
            user.setEmail("");
            user.setPassword("password");

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email cannot be empty")));
        }

        @Test
        public void testPasswordNotNull() {
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setPassword(null);

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password cannot be empty")));
        }

        @Test
        public void testPasswordNotBlank() {
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setPassword("");

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Password cannot be empty")));
        }

        @Test
        public void testFirstNameNotNull() {
            user.setFirstName(null);

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("First name cannot be empty")));
        }

        @Test
        public void testFirstNameNotBlank() {
            user.setFirstName("");

            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertEquals(1, violations.size());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("First name cannot be empty")));
        }
    }

    @Nested
    class ConstructorTests {
        @Test
        public void testConstructorWithFourParameters() {
            // Arrange
            String username = "testuser";
            String email = "testuser@example.com";
            String password = "password123";
            String firstName = "Test";

            // Act
            User user = new User(username, email, password, firstName);

            // Assert
            assertEquals(username, user.getUsername());
            assertEquals(email, user.getEmail());
            assertEquals(password, user.getPassword());
            assertEquals(firstName, user.getFirstName());
            assertNull(user.getLastName());
        }

        @Test
        public void testConstructorWithFiveParameters() {
            // Arrange
            String username = "testuser";
            String email = "testuser@example.com";
            String password = "password123";
            String firstName = "Test";
            String lastName = "User";

            // Act
            User user = new User(username, email, password, firstName, lastName);

            // Assert
            assertEquals(username, user.getUsername());
            assertEquals(email, user.getEmail());
            assertEquals(password, user.getPassword());
            assertEquals(firstName, user.getFirstName());
            assertEquals(lastName, user.getLastName());
        }
    }

    @Nested
    class GetterSetterTests {
        @Test
        public void testGetId() {
            user.setId(1L);
            assertEquals(1L, user.getId());
        }

        @Test
        public void testSetId() {
            user.setId(1L);
            assertEquals(1L, user.getId());
        }

        @Test
        public void testGetUsername() {
            user.setUsername("testuser");
            assertEquals("testuser", user.getUsername());
        }

        @Test
        public void testGetEmail() {
            user.setEmail("test@example.com");
            assertEquals("test@example.com", user.getEmail());
        }

        @Test
        public void testGetPassword() {
            user.setPassword("password");
            assertEquals("password", user.getPassword());
        }

        @Test
        public void testGetProvider() {
            user.setProvider("local");
            assertEquals("local", user.getProvider());
        }

        @Test
        public void testGetProviderId() {
            user.setProviderId("123456");
            assertEquals("123456", user.getProviderId());
        }

        @Test
        public void testGetFirstName() {
            user.setFirstName("John");
            assertEquals("John", user.getFirstName());
        }

        @Test
        public void testGetLastName() {
            user.setLastName("Doe");
            assertEquals("Doe", user.getLastName());
        }

        @Test
        public void testToString() {
            LocalDateTime lastLogin = LocalDateTime.now();
            String username = "testuser";
            String email = "test@example.com";
            String firstName = "John";
            String lastName = "Doe";
            Boolean isActive = true;

            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setLastLogin(lastLogin);
            user.setIsActive(true);

            String expected = String.format(
                    "username: %s, email: %s, first_name: %s, last_name: %s, last_login: %s, is_active: %s",
                    username, email, firstName, lastName, lastLogin, isActive);

            assertEquals(expected, user.toString());
        }
    }

}
