package com.example.unipiplishopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.widget.Button;


public class ProductListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Αρχικοποίηση RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadProducts(); // Δημιουργία λίστας προϊόντων

        // Δημιουργία και ορισμός adapter
        productAdapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(productAdapter);

        // Αρχικοποίηση κουμπιού για το καλάθι
        Button buttonGoToCart = findViewById(R.id.buttonGoToCart);
        buttonGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Άνοιγμα του CartActivity
                Intent intent = new Intent(ProductListActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadProducts() {
        productList = new ArrayList<>();

        // Προσθήκη προϊόντων (hardcoded προς το παρόν)
        productList.add(new Product("1", "Laptop", "Powerful laptop", 999.99, 1));
        productList.add(new Product("2", "Smartphone", "Latest model smartphone", 699.99, 1));
        productList.add(new Product("3", "Headphones", "Noise cancelling headphones", 199.99, 1));

        // Αποθήκευση εικόνων στο SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ProductImages", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("1", R.drawable.laptop);
        editor.putInt("2", R.drawable.smartphone);
        editor.putInt("3", R.drawable.headphones);
        editor.apply();
    }
}
