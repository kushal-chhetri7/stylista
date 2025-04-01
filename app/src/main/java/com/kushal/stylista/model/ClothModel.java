package com.kushal.stylista.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.kushal.stylista.utils.DummyData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClothModel implements Serializable {
    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String subcategory;
    private String brand;
    private List<Color> colors;
    private List<String> materials;
    private List<String> tags;
    private boolean isFavorite;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public List<String> getMaterials() {
        return materials;
    }

    public void setMaterials(List<String> materials) {
        this.materials = materials;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return DummyData.getDummyImage();
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClothModel that = (ClothModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Inner classes for Color, Rating, and Shipping
    public static class Color implements Serializable{
        private String name;
        private String hex;
        private List<String> images;
        private List<Size> sizes;

        // Getters and Setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHex() {
            return hex;
        }

        public void setHex(String hex) {
            this.hex = hex;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public List<Size> getSizes() {
            return sizes;
        }

        public void setSizes(List<Size> sizes) {
            this.sizes = sizes;
        }
    }

    public static class Size implements Serializable {
        private String size;
        private int stock;

        // Getters and Setters

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }
    }

}
