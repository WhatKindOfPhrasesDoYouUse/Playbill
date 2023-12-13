package com.vyatsu.playbill.services;

import com.vyatsu.playbill.models.Cart;
import com.vyatsu.playbill.models.Event;
import com.vyatsu.playbill.models.User;
import com.vyatsu.playbill.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CartService {
    public final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Page<Cart> getCartItemsForUser(User user, Pageable pageable) {
        return cartRepository.findByUser(user, pageable);
    }
    public Cart getCartByUser(User user) {
        return cartRepository.findCartByUser(user);
    }
    public void addToCart(User user, Event event, String paymentType) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setEvent(event);
        cart.setPaymentType(paymentType);
        cartRepository.save(cart);
    }

    public void removeFromCart(Long eventId, Long userId) {
        cartRepository.deleteByUserIdAndEventId(userId, eventId);
    }

    public Cart getCartItemById(Long id) {
        return cartRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    @Transactional
    public void removeAllCart(User user, List<Cart> itemsToRemove) {
        for (Cart cartItem : itemsToRemove) {
            cartRepository.delete(cartItem);
        }
    }
}
