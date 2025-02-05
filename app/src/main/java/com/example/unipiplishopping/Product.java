package com.example.unipiplishopping;

public class Product {
    private String id;
    private final String title;
    private final String description;
    private final String date;
    private final double price;
    private final int quantity;
    private final String storeLocation; // Νέο πεδίο για το κατάστημα

    public Product(String id, String title, String description, String date, double price, int quantity, String storeLocation) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.price = price;
        this.quantity = quantity;
        this.storeLocation = storeLocation;
    }

    // Getter και Setter για τα χαρακτηριστικά
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

}
