package com.unemployed.coreconnect.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.unemployed.coreconnect.model.entity.User;
import com.unemployed.coreconnect.service.UserService;



@Controller
public class MainController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/conversation")
    public String conversationPage() {
        return "conversation";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String getSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(@ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signup";
        }
        
        User savedUser = userService.saveUser(user);
        if (savedUser == null) {
            log.error("Error creating user: %s", user.toString());
            return "signup";
        }

        log.info(String.format("User created: %s", savedUser.toString()));

        return "login";
    }
    
    
}
