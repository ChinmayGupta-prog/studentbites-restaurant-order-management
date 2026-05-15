package com.studentbites.controller;

import com.studentbites.service.CartService;
import com.studentbites.repository.StudentOrderRepository;
import com.studentbites.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {
    private final CartService cartService;
    private final StudentOrderRepository orders;

    public GlobalModelAdvice(CartService cartService, StudentOrderRepository orders) {
        this.cartService = cartService;
        this.orders = orders;
    }

    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) {
        return cartService.count(session);
    }

    @ModelAttribute("drawerCartItems")
    public Object cartItems(HttpSession session) {
        return cartService.items(session);
    }

    @ModelAttribute("drawerCartTotal")
    public Object cartTotal(HttpSession session) {
        return cartService.total(session);
    }

    @ModelAttribute("currentUserName")
    public Object currentUserName(HttpSession session) {
        return session.getAttribute(AuthService.USER_NAME);
    }

    @ModelAttribute("currentUserEmail")
    public Object currentUserEmail(HttpSession session) {
        return session.getAttribute(AuthService.USER_EMAIL);
    }

    @ModelAttribute("currentUserHostel")
    public Object currentUserHostel(HttpSession session) {
        return session.getAttribute(AuthService.USER_HOSTEL);
    }

    @ModelAttribute("myOrderCount")
    public long myOrderCount(HttpSession session) {
        Object email = session.getAttribute(AuthService.USER_EMAIL);
        return email == null ? 0 : orders.countByEmailIgnoreCase(email.toString());
    }

}
