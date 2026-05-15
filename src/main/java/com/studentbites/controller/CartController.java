package com.studentbites.controller;

import com.studentbites.dto.CheckoutForm;
import com.studentbites.model.StudentOrder;
import com.studentbites.service.AuthService;
import com.studentbites.service.CartService;
import com.studentbites.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {
    private final CartService cartService;
    private final OrderService orderService;
    private final AuthService authService;

    public CartController(CartService cartService, OrderService orderService, AuthService authService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.authService = authService;
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        model.addAttribute("cartItems", cartService.items(session));
        model.addAttribute("cartTotal", cartService.total(session));
        if (!model.containsAttribute("checkoutForm")) {
            CheckoutForm checkoutForm = new CheckoutForm();
            applyLoggedInStudent(checkoutForm, session);
            model.addAttribute("checkoutForm", checkoutForm);
        }
        return "cart";
    }

    @PostMapping("/cart/update")
    public String update(@RequestParam Long itemId, @RequestParam int quantity, HttpSession session) {
        cartService.update(itemId, quantity, session);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@Valid @ModelAttribute CheckoutForm checkoutForm,
                           BindingResult result,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        var cartItems = cartService.items(session);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("toast", "Your cart is empty");
            return "redirect:/menu";
        }
        if (!authService.loggedIn(session)) {
            redirectAttributes.addFlashAttribute("toast", "Please login before checkout");
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            applyLoggedInStudent(checkoutForm, session);
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("cartTotal", cartService.total(session));
            model.addAttribute("toast", "Please complete the required checkout details");
            return "cart";
        }
        applyLoggedInStudent(checkoutForm, session);
        StudentOrder order = orderService.placeOrder(checkoutForm, cartItems);
        cartService.clear(session);
        redirectAttributes.addFlashAttribute("toast", "Order #" + order.getId() + " placed successfully");
        return "redirect:/invoice/" + order.getId();
    }

    private void applyLoggedInStudent(CheckoutForm checkoutForm, HttpSession session) {
        if (!authService.loggedIn(session)) {
            return;
        }
        checkoutForm.setStudentName(String.valueOf(session.getAttribute(AuthService.USER_NAME)));
        checkoutForm.setEmail(String.valueOf(session.getAttribute(AuthService.USER_EMAIL)));
        checkoutForm.setPhone(String.valueOf(session.getAttribute(AuthService.USER_PHONE)));
        Object hostel = session.getAttribute(AuthService.USER_HOSTEL);
        if (checkoutForm.getHostelOrClass() == null || checkoutForm.getHostelOrClass().isBlank()) {
            checkoutForm.setHostelOrClass(hostel == null ? "" : hostel.toString());
        }
    }
}
