package com.example.unipiplishopping;

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
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewPrice.setText("€" + product.getPrice());
        holder.textViewDescription.setText(product.getDescription());

        // Φόρτωση εικόνας από SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProductImages", Context.MODE_PRIVATE);
        int imageResId = sharedPreferences.getInt(product.getId(), R.drawable.laptop);
        holder.imageViewProduct.setImageResource(imageResId);

        // Φόρτωση ποσότητας από SharedPreferences
        SharedPreferences quantityPrefs = context.getSharedPreferences("ProductQuantities", Context.MODE_PRIVATE);
        final int[] quantity = {quantityPrefs.getInt(product.getId(), 0)};  // Αρχική ποσότητα 0
        holder.textViewQuantity.setText(String.valueOf(quantity[0]));

        // Ρυθμίσεις για τα κουμπιά + και -
        holder.buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity[0]++;  // Αυξάνει την ποσότητα
                holder.textViewQuantity.setText(String.valueOf(quantity[0]));  // Ενημερώνει την ποσότητα στο UI
                SharedPreferences.Editor editor = quantityPrefs.edit();
                editor.putInt(product.getId(), quantity[0]);  // Αποθήκευση νέας ποσότητας στο SharedPreferences
                editor.apply();
            }
        });

        holder.buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity[0] > 0) {  // Μειώνει την ποσότητα μόνο αν είναι μεγαλύτερη από 0
                    quantity[0]--;
                    holder.textViewQuantity.setText(String.valueOf(quantity[0]));
                    SharedPreferences.Editor editor = quantityPrefs.edit();
                    editor.putInt(product.getId(), quantity[0]);  // Αποθήκευση νέας ποσότητας στο SharedPreferences
                    editor.apply();
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
        Button buttonIncrease, buttonDecrease;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
        }
    }
}