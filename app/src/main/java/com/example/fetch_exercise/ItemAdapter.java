package com.example.fetch_exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList; // List of items to display

    // Constructor to initialize the adapter with data
    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    // Creates a new ViewHolder for the RecyclerView
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ItemViewHolder(view);
    }

    // Binds data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Get the item at the current position
        Item item = itemList.get(position);

        // Set the item's name in the TextView
        holder.textView.setText(item.getName() + " listID: " + item.getListID());
    }

    // Returns the total number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class to hold the views for each item in the RecyclerView
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView; // TextView to display the item name
        // Constructor to initialize the ViewHolder
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the TextView in the layout
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}