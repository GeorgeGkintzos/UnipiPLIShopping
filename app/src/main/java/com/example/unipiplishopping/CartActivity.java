package com.example.unipiplishopping;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private List<Product> cartList;
    private TextView textViewTotal;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartList = new ArrayList<>();
        textViewTotal = findViewById(R.id.textViewTotal);
        Button buttonCheckout = findViewById(R.id.buttonCheckout);

        loadCartProducts();

        cartAdapter = new CartAdapter(this, cartList);
        RecyclerView recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);

        calculateTotal();

        buttonCheckout.setOnClickListener(v -> {
            clearCart();
            Toast.makeText(CartActivity.this, "Η παραγγελία ολοκληρώθηκε!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    void loadCartProducts() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        SharedPreferences productPrefs = getSharedPreferences("ProductData", MODE_PRIVATE);

        cartList.clear();

        for (String productId : productPrefs.getAll().keySet()) {
            if (productId.endsWith("_title")) {
                String id = productId.replace("_title", "");
                int quantity = sharedPreferences.getInt(id + "_quantity", 0);
                if (quantity > 0) {
                    String title = productPrefs.getString(productId, "Άγνωστο");
                    String description = productPrefs.getString(id + "_description", "");
                    String date = productPrefs.getString(id + "_date", "");
                    double price = Double.parseDouble(productPrefs.getString(id + "_price", "0.0"));
                    cartList.add(new Product(id, title, description, date, price, quantity));
                }
            }
        }

        // Ενημέρωση του adapter με τη νέα λίστα
        if (cartAdapter != null) {
            cartAdapter.updateCartList(cartList);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void calculateTotal() {
        double total = 0;
        for (Product product : cartList) {
            total += product.getPrice() * product.getQuantity();
        }
        textViewTotal.setText("Σύνολο: €" + String.format("%.2f", total));
    }

    private void clearCart() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        cartList.clear();
        if (cartAdapter != null) {
            cartAdapter.updateCartList(cartList); // Ενημέρωση του adapter
        }
        calculateTotal();
    }

}