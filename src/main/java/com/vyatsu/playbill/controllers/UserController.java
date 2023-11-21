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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;

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
