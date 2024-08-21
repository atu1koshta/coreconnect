package com.unemployed.coreconnect.model;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
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
