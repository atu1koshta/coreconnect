package com.unemployed.coreconnect.factory;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.javafaker.Faker;
import com.unemployed.coreconnect.model.entity.User;

public class UserFactory {
    private static final EasyRandom easyRandom;
    private static final Faker faker;

    static {
        faker = new Faker();
        
        EasyRandomParameters parameters = new EasyRandomParameters().stringLengthRange(6, 12)
        .collectionSizeRange(1, 3);
        
        easyRandom = new EasyRandom(parameters);
    }

    public static User create() {
        User user = easyRandom.nextObject(User.class);
        user.setEmail(faker.internet().emailAddress());
        
        return user;
    }
}
