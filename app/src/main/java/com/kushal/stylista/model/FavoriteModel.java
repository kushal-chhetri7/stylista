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

    public static List<FavoriteModel> getDummyList(){
        String[] images = {"https://img.freepik.com/free-photo/interior-kids-room-decoration-with-clothes_23-2149096034.jpg?semt=ais_hybrid", "https://img.freepik.com/free-photo/still-life-with-classic-shirts-hanger_23-2150828629.jpg?semt=ais_hybrid","https://img.freepik.com/free-photo/woman-showing-clothes-customer_23-2148929526.jpg?semt=ais_hybrid","https://img.freepik.com/free-photo/still-life-with-classic-shirts-hanger_23-2150828620.jpg?t=st=1743093561~exp=1743097161~hmac=dd8220d85798c534f9afbbe2ba95c63de18d761f90beee8a11a582df08a85ecb&w=740","https://img.freepik.com/free-photo/still-life-say-no-fast-fashion_23-2149669576.jpg?t=st=1743093565~exp=1743097165~hmac=fb9a4253ade77248676674ec9cd25100d4aade5a8cb821274175422d3a23463b&w=740"};

        List<FavoriteModel> dummyList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            dummyList.add(new FavoriteModel(
                    String.valueOf(i),
                    "Product " + i,
                    (double) (Math.round(Math.random() * 10000.0) / 100.0), // Random price between 0.00 and 100.00
                    images[(int) (Math.random() * images.length)]
            ));
        }
        return dummyList;
    }
}
