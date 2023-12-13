package com.vyatsu.playbill.controllers;

import com.vyatsu.playbill.models.Cart;
import com.vyatsu.playbill.models.Event;
import com.vyatsu.playbill.models.User;
import com.vyatsu.playbill.repositories.CartRepository;
import com.vyatsu.playbill.repositories.UserRepository;
import com.vyatsu.playbill.services.CartService;
import com.vyatsu.playbill.services.EventService;
import com.vyatsu.playbill.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CartController {
    private final CartService cartService;
    public final UserRepository userRepository;

    private final EventService eventService;

    @Autowired
    public CartController(CartService cartService, UserRepository userRepository, EventService eventService) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.eventService = eventService;
    }

    @GetMapping("/cart")
    public String showCart(Model model, Principal principal,
                           @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 5) Pageable pageable) {
        User user = userRepository.findByUsername(principal.getName());
        Page<Cart> carts = cartService.getCartItemsForUser(user, pageable);
        int totalPrice = 0;
        for (Cart cartItem : carts) {
            totalPrice += cartItem.getEvent().getPrice();
        }
        model.addAttribute("carts", carts);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("event", new Event());
        return "cart";
    }

    @GetMapping("/add-cart/{id}")
    public String addToCart(@PathVariable(value = "id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUsername(principal.getName());
        Event event = eventService.getEventById(id);
        if (event != null) {
            if (event.getQuantity() <= 0) {
                redirectAttributes.addFlashAttribute("noTicketsMessage", "Билеты на данный спектакль закончились.");
            } else if (user.getAge() < event.getAgeLimit()) {
                redirectAttributes.addFlashAttribute("noAge", "Недостаточно лет для посещения данного мероприятия");
            } else {
                cartService.addToCart(user, event, "common");
            }
        }
        return "redirect:/cart";
    }

    @Transactional
    @GetMapping("/cart/remove-from-cart/{id}")
    public String removeCart(@PathVariable(value = "id") Long id, Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName());
        Cart cartItem = cartService.getCartItemById(id);
        if (cartItem != null && !cartItem.isPurchased()) {
            cartService.removeFromCart(id, user.getId());
            return "redirect:/cart";
        } else {
            model.addAttribute("error", "Нельзя удалить купленный билет");
        }
        return "redirect:/cart";
    }

    @Transactional
    @GetMapping("/cart/remove-all-cart")
    public String clearAllCart(Principal principal, Pageable pageable) {
        User user = userRepository.findByUsername(principal.getName());
        Page<Cart> cartItems = cartService.getCartItemsForUser(user, pageable);
        List<Cart> nonPurchasedItems = cartItems.stream().
                filter(cartItem -> !cartItem.isPurchased())
                .collect(Collectors.toList());
        cartService.removeAllCart(user, nonPurchasedItems);
        return "redirect:/cart";
    }

    @Transactional
    @PostMapping("/cart/purchase-item/{id}")
    public String purchaseItem(@PathVariable(value = "id") Long id,
                               @RequestParam(value = "paymentType") String paymentType,
                               Principal principal,
                               Model model) {
        User user = userRepository.findByUsername(principal.getName());
        Cart cartItem = cartService.getCartItemById(id);
        if (cartItem != null && cartItem.getUser().equals(user) && !cartItem.isPurchased()) {
            Event event = cartItem.getEvent();
            if ("pushkin".equals(paymentType) && (user.getAge() < 14 || user.getAge() > 22)) {
                model.addAttribute("error", "Ваш возраст не подходит для оплаты по Пушкинской карте");
                return "redirect:/cart";
            }
            int totalPrice;
            if (event.getQuantity() > 0) {
                if ("pushkin".equals(paymentType)) {
                    totalPrice = cartItem.getEvent().getPrice();
                    if (user.getPushkinskayaBalance() >= totalPrice) {
                        user.setPushkinskayaBalance(user.getPushkinskayaBalance() - totalPrice);
                    } else {
                        model.addAttribute("error", "Недостаточно средств на балансе Пушкинской карты");
                        return "redirect:/cart";
                    }
                } else {
                    totalPrice = cartItem.getEvent().getPrice();
                    if (user.getBalance() >= totalPrice) {
                        user.setBalance(user.getBalance() - totalPrice);
                    } else {
                        model.addAttribute("error", "Недостаточно средств на обычном балансе");
                        return "redirect:/cart";
                    }
                }
                cartItem.setPaymentType(paymentType);
                cartItem.setPurchased(true);
                cartService.save(cartItem);
                event.setQuantity(event.getQuantity() - 1);
                eventService.save(event);
                userRepository.save(user);
            } else {
                model.addAttribute("error", "Билеты на данное мероприятие закончились");
            }
        }
        return "redirect:/cart";
    }

    @Transactional
    @GetMapping("/cart/purchase-all-items")
    public String purchaseAllItems(Principal principal, Pageable pageable, Model model,
                                   @RequestParam(value = "paymentType") String paymentType) {
        User user = userRepository.findByUsername(principal.getName());
        Page<Cart> cartItemsPage = cartService.getCartItemsForUser(user, pageable);
        List<Cart> cartItems = cartItemsPage.stream().toList();
        int totalPrice = 0;
        for (Cart cartItem : cartItems) {
            totalPrice += cartItem.getEvent().getPrice();
        }
        if ("pushkin".equals(paymentType) && user.getPushkinskayaBalance() >= totalPrice) {
            user.setPushkinskayaBalance(user.getPushkinskayaBalance() - totalPrice);
            for (Cart cartItem : cartItems) {
                cartItem.setPurchased(true);
            }
        } else if ("common".equals(paymentType) && user.getBalance() >= totalPrice) {
            user.setBalance(user.getBalance() - totalPrice);
            for (Cart cartItem : cartItems) {
                cartItem.setPurchased(true);
            }
        }
        userRepository.save(user);
        cartItems.forEach(cartService::save);
        return "redirect:/cart";
    }


    @Transactional
    @GetMapping("/cart/refund/{id}")
    public String refund(@PathVariable(value = "id") Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        Cart cartItem = cartService.getCartItemById(id);
        if (cartItem != null && cartItem.getUser().equals(user) && cartItem.isPurchased()) {
            int totalPrice = cartItem.getEvent().getPrice();
            String paymentType = cartItem.getPaymentType();
            if ("pushkin".equals(paymentType)) {
                user.setPushkinskayaBalance(user.getPushkinskayaBalance() + totalPrice);
            } else {
                user.setBalance(user.getBalance() + totalPrice);
            }
            userRepository.save(user);
            cartItem.setPurchased(false);
            cartService.save(cartItem);
        }
        return "redirect:/cart";
    }
}
