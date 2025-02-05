package com.example.unipiplishopping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final Context context;
    private List<Product> cartList;

    public CartAdapter(Context context, List<Product> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    // Add a method to update the list
    @SuppressLint("NotifyDataSetChanged")
    public void updateCartList(List<Product> newCartList) {
        this.cartList = newCartList;
        notifyDataSetChanged(); // Notify the adapter for changes
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartList.get(position);
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewPrice.setText(String.format("%.2f", product.getPrice()) + "€");
        holder.textViewQuantity.setText(context.getString(R.string.quantity) + " " + product.getQuantity());

        // Display the product image
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProductImages", Context.MODE_PRIVATE);
        int imageResId = sharedPreferences.getInt(product.getId(), R.drawable.mayhem);
        holder.imageViewProduct.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewTitle, textViewQuantity, textViewPrice;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }
}