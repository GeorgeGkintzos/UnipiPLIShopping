package com.example.unipiplishopping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class CartActivity extends BaseActivity {
    private List<Product> cartList;
    private TextView textViewTotal;
    private CartAdapter cartAdapter;
    private Button orderDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Εφαρμογή του font size στα στοιχεία της δραστηριότητας
        applyFontSize();

        // Αρχικοποίηση των FloatingActionButtons
        FloatingActionButton fabSettings = findViewById(R.id.fabSettings);
        FloatingActionButton fabOption1 = findViewById(R.id.fabOption1);
        FloatingActionButton fabOption2 = findViewById(R.id.fabOption2);

        // Λειτουργία για το κύριο κουμπί Settings
        fabSettings.setOnClickListener(v -> {
            if (fabOption1.getVisibility() == View.GONE) {
                // Εμφάνιση των επιπλέον κουμπιών
                fabOption1.setVisibility(View.VISIBLE);
                fabOption2.setVisibility(View.VISIBLE);
            } else {
                // Απόκρυψη των επιπλέον κουμπιών
                fabOption1.setVisibility(View.GONE);
                fabOption2.setVisibility(View.GONE);
            }
        });

        // Λειτουργία για το πρώτο επιπλέον κουμπί
        fabOption1.setOnClickListener(v -> {
            Toast.makeText(this, "Option 1 Clicked", Toast.LENGTH_SHORT).show();
        });

        // Λειτουργία για το δεύτερο επιπλέον κουμπί
        fabOption2.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, SettingsActivity.class);
            startActivity(intent);        });

        cartList = new ArrayList<>();
        textViewTotal = findViewById(R.id.textViewTotal);
        Button orderDone = findViewById(R.id.buttonCheckout);

        loadCartProducts();

        cartAdapter = new CartAdapter(this, cartList);
        RecyclerView recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);

        calculateTotal();

        orderDone.setOnClickListener(v -> {
            String order_id=addOrderToDb();
            addOrderLinestoDB(order_id);
            clearCart();
            Toast.makeText(this, getString(R.string.order_completed), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void  addOrderLinestoDB(String order_id){
        SharedPreferences cartPrefs = getSharedPreferences("Cart", MODE_PRIVATE);
        SharedPreferences productPrefs = getSharedPreferences("ProductData", MODE_PRIVATE);

        for (String productId : productPrefs.getAll().keySet()) {
            if (productId.endsWith("_title")) {
                String id = productId.replace("_title", "");
                int quantity = cartPrefs.getInt(id + "_quantity", 0);
                if (quantity > 0) {
                    double price = Double.parseDouble(productPrefs.getString(id + "_price", "0.0"));

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orderlines");
                    String orderLineId = databaseReference.push().getKey(); // Δημιουργία μοναδικού order ID

                    if (orderLineId != null) {
                        // Δημιουργία HashMap με τα στοιχεία της γραμμης παραγγελίας
                        Map<String, Object> orderLineData = new HashMap<>();
                        orderLineData.put("order_id", order_id);
                        orderLineData.put("product_id", id);
                        orderLineData.put("quantity", quantity);
                        orderLineData.put("price", price);
                        orderLineData.put("total_price",quantity*price);

                        // Εισαγωγή στη Firebase
                        databaseReference.child(orderLineId).setValue(orderLineData)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(this, getString(R.string.order_line_success), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, getString(R.string.order_line_failure), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }


                }
            }
        }


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
                    String store = productPrefs.getString(id + "_store", "Άγνωστο κατάστημα");
                    double price = Double.parseDouble(productPrefs.getString(id + "_price", "0.0"));
                    cartList.add(new Product(id, title, description, date, price, quantity, store));
                }
            }
        }

        // Ενημέρωση του adapter με τη νέα λίστα
        if (cartAdapter != null) {
            cartAdapter.updateCartList(cartList);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private double calculateTotal() {
        double total = 0;
        for (Product product : cartList) {
            total += product.getPrice() * product.getQuantity();
        }
        textViewTotal.setText(getString(R.string.total_price) + ": " + String.format("%.2f", total)+ "€");
        return total;
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

    private String addOrderToDb() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        String orderId = databaseReference.push().getKey(); // Δημιουργία μοναδικού order ID

        if (orderId != null) {
            // Δημιουργία HashMap με τα στοιχεία του χρήστη
            Map<String, Object> orderData = new HashMap<>();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);


            String username=sharedPreferences.getString("username", null);
            double total=calculateTotal();
            orderData.put("username", username);
            orderData.put("datetime", timestamp);
            orderData.put("total_price", total);

            // Εισαγωγή στη Firebase
            databaseReference.child(orderId).setValue(orderData)

                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Toast.makeText(this, getString(R.string.order_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, getString(R.string.order_failure), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        return orderId;
    }

}
