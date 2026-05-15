package com.studentbites.model;

public enum FoodCategory {
    SOUTH_INDIAN("South Indian"),
    PIZZA("Pizza"),
    MEALS("Meals"),
    BURGERS("Burgers"),
    BIRYANI("Biryani"),
    BEVERAGES("Beverages"),
    SNACKS("Snacks"),
    DESSERTS("Desserts"),
    HEALTHY("Healthy");

    private final String label;

    FoodCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
