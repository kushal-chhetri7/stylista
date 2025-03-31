package com.kushal.stylista.model;

import java.util.ArrayList;

public class ClothModel {

    private final String id;
    private final String name;
    private final String category;
    private final double price;
    private final String description;
    private final String imageUrl;
    private final ArrayList<String> colors;
    private final ArrayList<String> sizes;

    public ClothModel(String id, String name, String category, double price, String description, String imageUrl, ArrayList<String> colors, ArrayList<String> sizes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.colors = colors;
        this.sizes = sizes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public ArrayList<String> getSizes() {
        return sizes;
    }
}
