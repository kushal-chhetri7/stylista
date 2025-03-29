package com.kushal.stylista.model;

import java.util.ArrayList;
import java.util.List;

public class FavoriteModel {

    private final String id;
    private final String title;
    private final double price;
    private final String imageUrl;

    public FavoriteModel(String id, String title, double price,  String imageUrl){
        this.id = id;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }


}
