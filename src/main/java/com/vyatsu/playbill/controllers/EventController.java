package com.vyatsu.playbill.controllers;

import com.vyatsu.playbill.LocalDateEditor;
import com.vyatsu.playbill.models.Event;
import com.vyatsu.playbill.models.User;
import com.vyatsu.playbill.repositories.EventRepository;
import com.vyatsu.playbill.repositories.UserRepository;
import com.vyatsu.playbill.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class EventController {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    @Autowired
    public EventController(UserRepository userRepository, EventRepository eventRepository, EventService eventService) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    @GetMapping()
    public String home(Model model,
                       Principal principal,
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 5) Pageable pageable,
                       @RequestParam(value = "title", required = false) String title,
                       @RequestParam(value = "minPrice", required = false) Integer minPrice,
                       @RequestParam(value = "maxPrice", required = false) Integer maxPrice) {
        Page<Event> events;
        events = eventService.filterEvents(title, minPrice, maxPrice, pageable);
        Event event = new Event();
        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("user_for_name", user);
        model.addAttribute("events", events);
        model.addAttribute("event", event);
        model.addAttribute("number_of_age", events.getTotalPages());
        if (events.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, (int) events.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumber", pageNumbers);
        }
        return "home";
    }


    @GetMapping("/remove-event/{id}")
    public String removeEvent(@PathVariable(value = "id") Long id) {
        eventService.deleteById(id);
        return "redirect:/";
    }


    @GetMapping("/event-info/{id}")
    public String eventInfo(@PathVariable(value = "id") Long id, Model model) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "event-info";
    }

    @GetMapping("/add-event")
    public String showAddEventForm() {
        return "add-event";
    }

    @PostMapping("/add-event")
    public String addEvent(@RequestParam(value = "title") String title,
                           @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           @RequestParam(value = "time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
                           @RequestParam(value = "duration") int duration,
                           @RequestParam(value = "location") String location,
                           @RequestParam(value = "price") int price,
                           @RequestParam(value = "ageLimit") int ageLimit,
                           @RequestParam(value = "quantity") int quantity) {
        Event event = new Event();
        event.setTitle(title);
        event.setDate(date);
        event.setTime(time);
        event.setDuration(duration);
        event.setLocation(location);
        event.setPrice(price);
        event.setAgeLimit(ageLimit);
        event.setQuantity(quantity);
        eventService.save(event);
        return "redirect:/";
    }

    @GetMapping("/edit-event/{id}")
    public String showEditEventForm(Model model, @PathVariable(value = "id") Long id) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        eventService.deleteById(id);
        return "event-edit";
    }

    @PostMapping("/change")
    public String editEvent(@ModelAttribute(value = "event") Event event) {
        eventService.save(event);
        return "redirect:/";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        binder.registerCustomEditor(LocalDate.class, new LocalDateEditor(dateFormatter));
    }
}

