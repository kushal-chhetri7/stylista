package com.kushal.stylista.model;

import com.google.firebase.firestore.DocumentSnapshot;

public class CartModel {

    private String id;
    private String userId;
    private String clothId;
    private int count;
    private String size;
    private String color;
    private ClothModel clothModel;

    public CartModel() {

    }

    public CartModel(String id, String userId, String clothId, int count, String size, String color, ClothModel clothModel) {
        this.id = id;
        this.userId = userId;
        this.clothId = clothId;
        this.count = count;
        this.size = size;
        this.color = color;
        this.clothModel = clothModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClothId() {
        return clothId;
    }

    public void setClothId(String clothId) {
        this.clothId = clothId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ClothModel getClothModel() {
        return clothModel;
    }

    public void setClothModel(ClothModel clothModel) {
        this.clothModel = clothModel;
    }

    public double getSubtotal() {
        return clothModel.getPrice() * count;
    }

    // Method to convert CartModel to OrderItem
    public OrderModel.OrderItem toOrderItem() {
        return new OrderModel.OrderItem(this.clothId, this.color, this.size, this.count, this.clothModel.getPrice());
    }

    public static CartModel fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        CartModel cartModel = documentSnapshot.toObject(CartModel.class);
        cartModel.setId(documentSnapshot.getId());
        return cartModel;
    }


}
