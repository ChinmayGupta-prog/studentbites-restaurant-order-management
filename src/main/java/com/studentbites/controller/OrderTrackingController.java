package com.studentbites.controller;

import com.studentbites.model.OrderStatus;
import com.studentbites.service.AuthService;
import com.studentbites.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderTrackingController {
    private final OrderService orderService;
    private final AuthService authService;

    public OrderTrackingController(OrderService orderService, AuthService authService) {
        this.orderService = orderService;
        this.authService = authService;
    }

    @GetMapping("/track")
    public String track(@RequestParam(required = false) Long orderId,
                        Model model,
                        HttpSession session) {
        if (orderId != null) {
            orderService.findByIdWithLiveStatus(orderId).ifPresent(order -> {
                model.addAttribute("trackedOrder", order);
                model.addAttribute("autoRefresh", order.getStatus() != OrderStatus.DELIVERED);
            });
        }
        if (authService.loggedIn(session)) {
            model.addAttribute("myOrders", orderService.latestForStudent(
                    String.valueOf(session.getAttribute(AuthService.USER_EMAIL))));
        }
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("orderId", orderId);
        return "track";
    }
}
