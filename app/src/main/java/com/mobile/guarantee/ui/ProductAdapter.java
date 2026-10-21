package com.mobile.guarantee.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.guarantee.R;
import com.mobile.guarantee.data.Product;

import java.util.concurrent.TimeUnit;

public class ProductAdapter
        extends ListAdapter<Product, ProductAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final OnItemClickListener listener;

    public ProductAdapter(OnItemClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Product> DIFF =
            new DiffUtil.ItemCallback<Product>() {
                @Override
                public boolean areItemsTheSame(@NonNull Product a, @NonNull Product b) {
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Product a, @NonNull Product b) {
                    return a.name.equals(b.name)
                            && a.store.equals(b.store)
                            && a.warrantyEndDate == b.warrantyEndDate;
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = getItem(position);
        holder.name.setText(p.name);
        holder.store.setText(p.store);

        long daysLeft = TimeUnit.MILLISECONDS.toDays(
                p.warrantyEndDate - System.currentTimeMillis());
        if (daysLeft < 0) {
            holder.status.setText("Expired");
            holder.status.setTextColor(Color.parseColor("#C62828"));
        } else if (daysLeft <= 30) {
            holder.status.setText(daysLeft + " days left");
            holder.status.setTextColor(Color.parseColor("#EF6C00"));
        } else {
            holder.status.setText(daysLeft + " days left");
            holder.status.setTextColor(Color.parseColor("#2E7D32"));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(p));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView store;
        final TextView status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            store = itemView.findViewById(R.id.textStore);
            status = itemView.findViewById(R.id.textStatus);
        }
    }
}