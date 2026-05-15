package com.studentbites.controller;

import com.studentbites.model.FoodCategory;
import com.studentbites.repository.FoodItemRepository;
import com.studentbites.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;

@Controller
public class MenuController {
    private final FoodItemRepository foodItems;
    private final CartService cartService;

    public MenuController(FoodItemRepository foodItems, CartService cartService) {
        this.foodItems = foodItems;
        this.cartService = cartService;
    }

    @GetMapping("/menu")
    public String menu(@RequestParam(required = false) FoodCategory category,
                       @RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "popular") String sort,
                       Model model) {
        var items = (category == null ? foodItems.findAll() : foodItems.findByCategory(category)).stream();
        if (q != null && !q.isBlank()) {
            String query = q.trim().toLowerCase();
            items = items.filter(item -> item.getName().toLowerCase().contains(query)
                    || item.getDescription().toLowerCase().contains(query)
                    || item.getCategory().getLabel().toLowerCase().contains(query));
        }
        Comparator<com.studentbites.model.FoodItem> comparator = switch (sort) {
            case "price-low" -> Comparator.comparing(com.studentbites.model.FoodItem::getPrice);
            case "price-high" -> Comparator.comparing(com.studentbites.model.FoodItem::getPrice).reversed();
            case "rating" -> Comparator.comparing(com.studentbites.model.FoodItem::getRating).reversed();
            default -> Comparator.comparing(com.studentbites.model.FoodItem::getRating).reversed();
        };
        model.addAttribute("items", items.sorted(comparator).toList());
        model.addAttribute("categories", FoodCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("sort", sort);
        return "menu";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            HttpServletRequest request,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        cartService.add(id, session);
        redirectAttributes.addFlashAttribute("toast", "Added to cart");
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer == null || referer.isBlank() ? "/menu" : referer);
    }
}
