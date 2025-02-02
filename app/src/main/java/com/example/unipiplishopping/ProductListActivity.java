package com.example.unipiplishopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    //Accessed only within the class itself
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list); //Defines the layout of the activity

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //Arranges items in a linear list (vertically or horizontally)

        loadProducts();

        if (productList != null && !productList.isEmpty()) {
            productAdapter = new ProductAdapter(this, productList);
            recyclerView.setAdapter(productAdapter); //RecyclerView, you need to connect it to an Adapter and a LayoutManager
        }

        Button buttonGoToCart = findViewById(R.id.buttonGoToCart);
        buttonGoToCart.setOnClickListener(v -> {
            if (productAdapter != null) {
                startActivity(new Intent(ProductListActivity.this, CartActivity.class));
            } else {
                Toast.makeText(ProductListActivity.this, "Δεν υπάρχουν διαθέσιμα προϊόντα.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        productList.add(new Product("1", "Lady Gaga\nMayhem", "Dark Pop", "7 March 2025", 43.00, 1));
        productList.add(new Product("2", "The Beatles\nAbbey Road", "Rock", "26 September 1969",35.00, 1));
        productList.add(new Product("3", "The Beatles\nLet It Be", "Rock", "8 May 1970", 35.00, 1));
        productList.add(new Product("4", "Panic! At The Disco\nDeath of a Bachelor", "Alternative Indie", "15 January 2016", 20.00, 1));
        productList.add(new Product("5", "Mac Miller\nBalloonerism", "Hip-Hop, Rap", "17 January 2025", 40.00, 1));
        productList.add(new Product("6", "Μάνος Χατζιδάκις\nΟ Μεγάλος Ερωτικός", "Folk, Classical, Entekhno", "March 1972", 45.00, 1));
        productList.add(new Product("7", "Τάνια Τσανακλίδου\nΜαμά Γερνάω", "Folk, Entekhno", "October 1981", 15.00, 1));
        productList.add(new Product("8", "Queen\nA Night at the Opera", "Rock, Pop", "21 November 1975", 38.00, 1));

        SharedPreferences sharedPreferences = getSharedPreferences("ProductData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Product product : productList) {
            editor.putString(product.getId() + "_title", product.getTitle());
            editor.putString(product.getId() + "_description", product.getDescription());
            editor.putString(product.getId() + "_price", String.valueOf(product.getPrice()));
        }
        editor.apply();

        SharedPreferences imagePrefs = getSharedPreferences("ProductImages", MODE_PRIVATE);
        SharedPreferences.Editor imageEditor = imagePrefs.edit();
        imageEditor.putInt("1", R.drawable.mayhem);
        imageEditor.putInt("2", R.drawable.abbey);
        imageEditor.putInt("3", R.drawable.let);
        imageEditor.putInt("4", R.drawable.death);
        imageEditor.putInt("5", R.drawable.ball);
        imageEditor.putInt("6", R.drawable.megalos);
        imageEditor.putInt("7", R.drawable.mama);
        imageEditor.putInt("8", R.drawable.queen);
        imageEditor.apply();
    }
}
