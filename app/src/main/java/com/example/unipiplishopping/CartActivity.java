package com.example.unipiplishopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private List<Product> cartList;  // Ο κατάλογος των προϊόντων στο καλάθι
    private TextView textViewTotal;
    private Button buttonCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartList = new ArrayList<>();
        textViewTotal = findViewById(R.id.textViewTotal);
        buttonCheckout = findViewById(R.id.buttonCheckout);

        // Προσθήκη προϊόντων στο καλάθι από SharedPreferences (ως παράδειγμα)
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        cartList.add(new Product("1", "Laptop", "Powerful laptop", 999.99, sharedPreferences.getInt("1_quantity", 0)));
        cartList.add(new Product("2", "Smartphone", "Latest model smartphone", 699.99, sharedPreferences.getInt("2_quantity", 0)));

        calculateTotal(); // Υπολογισμός του συνολικού κόστους

        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Εδώ μπορείς να προσθέσεις την λογική ολοκλήρωσης της παραγγελίας
                Toast.makeText(CartActivity.this, "Η παραγγελία ολοκληρώθηκε!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotal() {
        double total = 0;
        for (Product product : cartList) {
            total += product.getPrice() * product.getQuantity();
        }
        textViewTotal.setText("Total: €" + total);
    }
}
