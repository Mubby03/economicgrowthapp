package com.example.economicgrowthapp.searchdata;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economicgrowthapp.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private final Context context;
    private final List<WorldBankIndicator> dataList;
    private final OnItemClickListener listener;

    // Constructor that accepts an OnItemClickListener
    public SearchAdapter(Context context, List<WorldBankIndicator> dataList, OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener; // Assign the listener parameter
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorldBankIndicator indicator = dataList.get(position);

        holder.seriesNameTextView.setText(indicator.getSeriesName());
        holder.countryNameTextView.setText(indicator.getCountryName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Define an interface for the click listener
    public interface OnItemClickListener {
        void onItemClick(WorldBankIndicator indicator);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView seriesNameTextView;
        TextView countryNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            seriesNameTextView = itemView.findViewById(R.id.textSeriesName);
            countryNameTextView = itemView.findViewById(R.id.textCountryName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(dataList.get(position));
                }
            });
        }
    }
}

