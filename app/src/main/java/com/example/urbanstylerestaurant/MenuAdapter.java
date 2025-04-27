package com.example.urbanstylerestaurant;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private Context context;
    private List<MenuItem> itemList;

    public MenuAdapter(Context context, List<MenuItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price;
        public ImageView imageView;
        public Button buyButton;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.itemName);
            price = view.findViewById(R.id.itemPrice);
            imageView = view.findViewById(R.id.itemImage);
            buyButton = view.findViewById(R.id.buyButton);
        }
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        MenuItem item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.price.setText("R" + item.getPrice());

        String imageResName = item.getImageResName();
        if (imageResName != null && !imageResName.isEmpty()) {
            int imageResId = context.getResources().getIdentifier(imageResName, "drawable", context.getPackageName());
            if (imageResId != 0) {
                holder.imageView.setImageResource(imageResId);
            } else {
                Log.e("MenuAdapter", "Invalid image resource: " + imageResName);
                holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery); // Fallback image
            }
        } else {
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery); // Fallback image
        }

        // Set click listener for the Order button
        holder.buyButton.setOnClickListener(v -> {
            Log.d("MenuAdapter", "Order button clicked for item: " + item.getName());
            CartManager.addItem(item.getName(), item.getPrice());
            Toast.makeText(context, item.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}