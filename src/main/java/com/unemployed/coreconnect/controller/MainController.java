package com.unemployed.coreconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/conversation")
    public String redirectToConversation() {
        return "redirect:/conversation.html";
    }
}
