package com.unemployed.coreconnect.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping(value="/signup")
    public String handleSignup(@ModelAttribute User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        User savedUser;

        try{
            savedUser = userService.saveUser(user);
            log.info(String.format("User created: %s", savedUser.toString()));
            redirectAttributes.addFlashAttribute("success", "User created successfully. Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        } catch (UnexpectedRollbackException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to create user. Please try again.");
            return "redirect:/signup";
        }
    }
}
