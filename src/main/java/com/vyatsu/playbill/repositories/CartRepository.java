package com.vyatsu.playbill.repositories;

import com.vyatsu.playbill.models.Cart;
import com.vyatsu.playbill.models.Event;
import com.vyatsu.playbill.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Page<Cart> findByUser(User user, Pageable pageable);
    void deleteByUserIdAndEventId(Long userId, Long eventId);
    Cart findCartByUser(User user);

}
