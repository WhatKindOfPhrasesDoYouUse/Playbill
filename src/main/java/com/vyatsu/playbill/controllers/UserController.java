package com.vyatsu.playbill.controllers;

import com.vyatsu.playbill.models.Event;
import com.vyatsu.playbill.models.User;
import com.vyatsu.playbill.repositories.UserRepository;
import com.vyatsu.playbill.services.CartService;
import com.vyatsu.playbill.services.EventService;
import com.vyatsu.playbill.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, EventService eventService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping("/user-profile")
    public String userProfile(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user-profile";
    }

    @PostMapping("/user-profile/up-balance")
    public String upBalance(@RequestParam(value = "amount") int amount,
                            Model model,
                            Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
        return "redirect:/user-profile";
    }
}
