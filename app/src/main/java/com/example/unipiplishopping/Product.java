package com.example.unipiplishopping;

public class Product {
    private String id;
    private String title;
    private String description;
    private double price;
    private int quantity;

    public Product(String id, String title, String description, double price, int quantity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
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


    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

}
