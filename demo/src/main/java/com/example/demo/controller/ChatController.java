package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.User;
import com.example.demo.reop.ChatMessageRepository;
import com.example.demo.reop.UserRepository;
import com.example.demo.service.ChatService;
@Controller
public class ChatController {

    private final ChatService chatService;
    private final ChatMessageRepository chatRepository;
    private final UserRepository userRepository;

    public ChatController(ChatService chatService,
                          ChatMessageRepository chatRepository,
                          UserRepository userRepository) {
        this.chatService = chatService;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    
    @GetMapping("/chat")
    public String chatPage(Model model, Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        List<ChatMessage> chatHistory =
                chatRepository.findByUserOrderByTimestampAsc(user);

        model.addAttribute("chatHistory", chatHistory);
        model.addAttribute("username", username);

        return "chat";
    }

    @PostMapping("/chat")
    public String sendMessage(@RequestParam String message,
                              Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow();

        chatService.getResponse(user, message);

        return "redirect:/chat";
    }
}
