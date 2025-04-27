package com.example.urbanstylerestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<String> cartItems;

    public CartAdapter(Context context, List<String> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemText;

        public ViewHolder(View view) {
            super(view);
            itemText = view.findViewById(R.id.cartItemText);
        }
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        String item = cartItems.get(position);
        holder.itemText.setText(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }
}