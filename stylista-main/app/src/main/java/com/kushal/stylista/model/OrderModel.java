package com.kushal.stylista.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class OrderModel {

    private String id;
    private String userId;
    private List<OrderItem> items;
    private double totalAmount;
    private String status;
    private String paymentStatus;
    private String paymentId;
    @ServerTimestamp
    private Date createdAt;  // Use Firestore's server-side timestamp

    public OrderModel(){

    }

    public OrderModel(String userId, List<OrderItem> items, double totalAmount, String status, String paymentStatus, String paymentId) {
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentId = paymentId;
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

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public static OrderModel fromDocumentSnapshot(DocumentSnapshot document) {
        OrderModel order = document.toObject(OrderModel.class);
        order.setId(document.getId());
        return order;
    }

    // Nested OrderItem class for each item in the order
    public static class OrderItem {
        private String clothId;
        private String color;
        private String size;
        private int count;
        private double price;

        public OrderItem(){

        }

        public OrderItem(String clothId, String color, String size, int count, double price) {
            this.clothId = clothId;
            this.color = color;
            this.size = size;
            this.count = count;
            this.price = price;
        }

        public String getClothId() {
            return clothId;
        }

        public String getColor() {
            return color;
        }

        public String getSize() {
            return size;
        }

        public int getCount() {
            return count;
        }

        public double getPrice() {
            return price;
        }
    }
}
