package com.studentbites;

import com.studentbites.model.FoodCategory;
import com.studentbites.model.FoodItem;
import com.studentbites.model.Review;
import com.studentbites.repository.FoodItemRepository;
import com.studentbites.repository.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class StudentBitesApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentBitesApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(FoodItemRepository foodItems,
                               ReviewRepository reviews) {
        return args -> {
            List<FoodItem> menuItems = List.of(
                        item("Campus Masala Dosa", FoodCategory.SOUTH_INDIAN, "Crispy dosa with potato masala, coconut chutney and sambar.", "/images/menu-photos/masala-dosa.png", 79, 4.8, true, "12 min"),
                        item("Cheese Burst Pizza", FoodCategory.PIZZA, "Mozzarella loaded pizza with corn, onion, peppers and herbs.", "/images/menu-photos/cheese-pizza.png", 189, 4.7, true, "18 min"),
                        item("Paneer Tikka Bowl", FoodCategory.MEALS, "Smoky paneer, jeera rice, salad and mint yoghurt.", "/images/menu-photos/paneer-bowl.png", 159, 4.9, true, "15 min"),
                        item("Student Burger Combo", FoodCategory.BURGERS, "Veg burger, peri peri fries and chilled cola.", "/images/menu-photos/veggie-burger.png", 129, 4.6, false, "14 min"),
                        item("Hyderabadi Veg Biryani", FoodCategory.BIRYANI, "Aromatic basmati rice with vegetables, raita and salan.", "/images/menu-photos/biryani-bowl.png", 149, 4.8, true, "20 min"),
                        item("Cold Coffee Cloud", FoodCategory.BEVERAGES, "Cafe-style cold coffee topped with vanilla foam.", "/images/menu-photos/cold-coffee.png", 99, 4.5, true, "7 min"),
                        item("Momo Mania Platter", FoodCategory.SNACKS, "Steamed momos with red chilli dip and mayo.", "/images/menu-photos/steamed-momos.png", 119, 4.7, true, "11 min"),
                        item("Chocolate Waffle", FoodCategory.DESSERTS, "Belgian waffle with chocolate sauce, brownie crumbs and ice cream.", "/images/menu-photos/chocolate-waffle.png", 139, 4.9, false, "10 min"),
                        item("Protein Salad Box", FoodCategory.HEALTHY, "Chickpeas, paneer, sprouts, veggies and lemon dressing.", "/images/menu-photos/fresh-salad.png", 129, 4.4, true, "8 min"),
                        item("Maggi Tadka Bowl", FoodCategory.SNACKS, "The hostel classic upgraded with vegetables and cheese.", "/images/menu-photos/maggi-noodles.png", 69, 4.6, true, "6 min"),
                        item("Peri Peri Fries Cup", FoodCategory.SNACKS, "Crispy fries tossed with peri peri spice and cafe dip.", "/images/menu-photos/loaded-fries.png", 89, 4.5, true, "8 min"),
                        item("Tandoori Sandwich", FoodCategory.SNACKS, "Grilled sandwich with tandoori mayo, vegetables and cheese.", "/images/menu-photos/grilled-sandwich.png", 109, 4.6, true, "10 min"),
                        item("Rajma Rice Comfort Bowl", FoodCategory.MEALS, "Homestyle rajma, steamed rice, onion salad and pickle.", "/images/menu-photos/rajma-chawal.png", 119, 4.7, true, "13 min"),
                        item("Masala Chai Flask", FoodCategory.BEVERAGES, "Fresh ginger-cardamom chai for study groups.", "/images/menu-photos/masala-chai.png", 49, 4.8, true, "5 min"),
                        item("Mango Shake", FoodCategory.BEVERAGES, "Thick mango shake blended with vanilla ice cream.", "/images/menu-photos/mango-shake.png", 129, 4.7, true, "7 min"),
                        item("Mini Idli Sambar Tub", FoodCategory.SOUTH_INDIAN, "Soft mini idlis soaked in hot sambar with podi.", "/images/menu-photos/idli-sambar.png", 79, 4.5, true, "9 min")
            );

            for (FoodItem menuItem : menuItems) {
                foodItems.findByName(menuItem.getName()).ifPresentOrElse(existing -> {
                    existing.setCategory(menuItem.getCategory());
                    existing.setDescription(menuItem.getDescription());
                    existing.setImageUrl(menuItem.getImageUrl());
                    existing.setPrice(menuItem.getPrice());
                    existing.setRating(menuItem.getRating());
                    existing.setVegetarian(menuItem.isVegetarian());
                    existing.setPrepTime(menuItem.getPrepTime());
                    foodItems.save(existing);
                }, () -> foodItems.save(menuItem));
            }

            if (reviews.count() == 0) {
                reviews.saveAll(List.of(
                        new Review("Aarav", "B.Tech CSE", "Fast delivery between lectures and the paneer bowl tastes premium.", 5),
                        new Review("Meera", "MBA", "Order tracking is super useful during lunch rush. The interface feels like a real food app.", 5),
                        new Review("Kabir", "BCA", "Best budget menu around campus. The combo offers are perfect for students.", 4),
                        new Review("Nisha", "Design", "Beautiful layout, clear menu filters and the desserts look irresistible.", 5)
                ));
            }

        };
    }

    private static FoodItem item(String name, FoodCategory category, String description, String imageUrl,
                                 int price, double rating, boolean vegetarian, String prepTime) {
        FoodItem item = new FoodItem();
        item.setName(name);
        item.setCategory(category);
        item.setDescription(description);
        item.setImageUrl(imageUrl);
        item.setPrice(BigDecimal.valueOf(price));
        item.setRating(rating);
        item.setVegetarian(vegetarian);
        item.setPrepTime(prepTime);
        return item;
    }

}
