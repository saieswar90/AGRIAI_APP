package com.example.myapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {
    private List<Price1> prices;

    public PriceAdapter(List<Price1> prices) {
        this.prices = prices;
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price_user, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        Price1 price = prices.get(position);
        holder.commodity.setText("Commodity: " + price.getCommodity());
        holder.variety.setText("Variety: " + price.getVariety());
        holder.maxPrice.setText("Max: ₹" + price.getMaxPrice());
        holder.avgPrice.setText("Avg: ₹" + price.getAvgPrice());
        holder.minPrice.setText("Min: ₹" + price.getMinPrice());
    }

    @Override
    public int getItemCount() {
        return prices.size();
    }

    static class PriceViewHolder extends RecyclerView.ViewHolder {
        TextView commodity, variety, maxPrice, avgPrice, minPrice;

        public PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            commodity = itemView.findViewById(R.id.commodity);
            variety = itemView.findViewById(R.id.variety);
            maxPrice = itemView.findViewById(R.id.maxPrice);
            avgPrice = itemView.findViewById(R.id.avgPrice);
            minPrice = itemView.findViewById(R.id.minPrice);
        }
    }
}