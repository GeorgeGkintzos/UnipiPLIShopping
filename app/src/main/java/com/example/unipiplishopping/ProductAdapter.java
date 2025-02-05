package com.example.unipiplishopping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final Context context;
    private final List<Product> productList;

    // Automatically called, when a new object of the class is created.
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context; //To load a layout
        this.productList = productList; //Data to be displayed in RecyclerView
    }

    // Called by RecyclerView when it needs to create a new ViewHolder
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Ανάκτηση του αποθηκευμένου font size από τα SharedPreferences
        SharedPreferences fontPrefs = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        float fontSize = fontPrefs.getFloat("font_size", 16);

        // Εφαρμογή του font size στα TextView του item
        holder.textViewTitle.setTextSize(fontSize);
        holder.textViewPrice.setTextSize(fontSize);
        holder.textViewDescription.setTextSize(fontSize);
        holder.textViewQuantity.setTextSize(fontSize);
        holder.textViewReleaseDate.setTextSize(fontSize);
        holder.textViewStoreLocation.setTextSize(fontSize);

        // Συνέχεια με τις υπόλοιπες ενέργειες
        Product product = productList.get(position);
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewPrice.setText(product.getPrice() + "€");
        holder.textViewDescription.setText(product.getDescription());
        holder.textViewReleaseDate.setText(context.getString(R.string.release_date_label) + "\n" + product.getDate());
        holder.textViewStoreLocation.setText(context.getString(R.string.store_label) + "\n" + product.getStoreLocation());

        SharedPreferences sharedPreferences = context.getSharedPreferences("ProductImages", Context.MODE_PRIVATE);
        int imageResId = sharedPreferences.getInt(product.getId(), R.drawable.mayhem);
        holder.imageViewProduct.setImageResource(imageResId);

        SharedPreferences quantityPrefs = context.getSharedPreferences("Cart", Context.MODE_PRIVATE);
        final int[] quantity = {quantityPrefs.getInt(product.getId() + "_quantity", 0)};
        holder.textViewQuantity.setText(String.valueOf(quantity[0]));

        holder.buttonIncrease.setOnClickListener(v -> {
            quantity[0]++;
            holder.textViewQuantity.setText(String.valueOf(quantity[0]));
            SharedPreferences.Editor editor = quantityPrefs.edit();
            editor.putInt(product.getId() + "_quantity", quantity[0]);
            editor.apply();

            // Update CartActivity (if open)
            if (context instanceof CartActivity) {
                ((CartActivity) context).loadCartProducts();
            }
        });

        holder.buttonDecrease.setOnClickListener(v -> {
            if (quantity[0] > 0) {
                quantity[0]--;
                holder.textViewQuantity.setText(String.valueOf(quantity[0]));
                SharedPreferences.Editor editor = quantityPrefs.edit();
                editor.putInt(product.getId() + "_quantity", quantity[0]);
                editor.apply();

                // Update CartActivity (if open)
                if (context instanceof CartActivity) {
                    ((CartActivity) context).loadCartProducts();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewTitle, textViewPrice, textViewDescription, textViewQuantity;
        TextView textViewReleaseDate, textViewStoreLocation; // Νέα πεδία
        Button buttonIncrease, buttonDecrease;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewReleaseDate = itemView.findViewById(R.id.textViewReleaseDate);
            textViewStoreLocation = itemView.findViewById(R.id.textViewStoreLocation);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
        }
    }

}