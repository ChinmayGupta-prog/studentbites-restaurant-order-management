package com.studentbites.controller;

import com.studentbites.model.FoodCategory;
import com.studentbites.repository.FoodItemRepository;
import com.studentbites.repository.ReviewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final FoodItemRepository foodItems;
    private final ReviewRepository reviews;

    public HomeController(FoodItemRepository foodItems, ReviewRepository reviews) {
        this.foodItems = foodItems;
        this.reviews = reviews;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredItems", foodItems.findAll().stream().limit(8).toList());
        model.addAttribute("reviews", reviews.findAll());
        model.addAttribute("categories", FoodCategory.values());
        return "home";
    }
}
