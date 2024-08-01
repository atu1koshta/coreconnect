package com.unemployed.coreconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class MainController {
    @GetMapping("/")
    public String homePage() {
        return "redirect:/index.html";
    }
    

    @GetMapping("/conversation")
    public String conversationPage() {
        return "redirect:/conversation.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/login.html";
    }
}
