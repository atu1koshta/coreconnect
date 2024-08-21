package com.unemployed.coreconnect.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.unemployed.coreconnect.model.entity.User;

public class UserFactoryTest {
    @Test
    public void testCreateUser() {
        User user = UserFactory.create();
        
        assertNotNull(user);

        // Optionally, assert some properties of the User object
        assertNotNull(user.getUsername());
        assertNotNull(user.getEmail());
        assertTrue(user.getUsername().length() >= 6 && user.getUsername().length() <= 12);
    }
}
