package com.example.unipiplishopping;

public class Store {
    private String name;
    private String address; // Ενημέρωσε το όνομα ώστε να ταιριάζει με τη Firebase

    // Κενός constructor για Firebase
    public Store() {
    }

    public Store(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        if (address != null && address.contains(",")) {
            try {
                return Double.parseDouble(address.split(",")[0].trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }

    public double getLongitude() {
        if (address != null && address.contains(",")) {
            try {
                return Double.parseDouble(address.split(",")[1].trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }
}
