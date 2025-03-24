package com.example.myapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyDataAdapter extends RecyclerView.Adapter<MyDataAdapter.MyDataViewHolder> {
    private List<Crop> crops;

    public MyDataAdapter(List<Crop> crops) {
        this.crops = crops;
    }

    @NonNull
    @Override
    public MyDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mydata, parent, false);
        return new MyDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDataViewHolder holder, int position) {
        Crop crop = crops.get(position);

        // Set crop details
        holder.cropName.setText(crop.getCropName());
        holder.year.setText(String.valueOf(crop.getYear()));

        // Set individual spending details
        holder.tractorRentCost.setText(String.format("Tractor Rent Cost: ₹%.2f", crop.getTractorRentCost()));
        holder.labourerCost.setText(String.format("Labourer Cost: ₹%.2f", crop.getLabourerCost()));
        holder.fertilizerCost.setText(String.format("Fertilizer Cost: ₹%.2f", crop.getFertilizerCost()));
        holder.pesticideCost.setText(String.format("Pesticide Cost: ₹%.2f", crop.getPesticideCost()));
        holder.harvestingCost.setText(String.format("Harvesting Cost: ₹%.2f", crop.getHarvestingCost()));

        // Calculate total cost
        double totalCost = crop.getTractorRentCost() + crop.getLabourerCost() + crop.getFertilizerCost() +
                crop.getPesticideCost() + crop.getHarvestingCost();
        holder.totalCost.setText(String.format("Total Cost: ₹%.2f", totalCost));

        // Calculate profit/loss
        double profitOrLoss = crop.getAmountSold() - totalCost;
        holder.profitOrLoss.setText(profitOrLoss >= 0 ?
                String.format("Profit: ₹%.2f", profitOrLoss) :
                String.format("Loss: ₹%.2f", Math.abs(profitOrLoss)));
    }

    @Override
    public int getItemCount() {
        return crops.size();
    }

    static class MyDataViewHolder extends RecyclerView.ViewHolder {
        TextView cropName, year, tractorRentCost, labourerCost, fertilizerCost, pesticideCost, harvestingCost, totalCost, profitOrLoss;

        public MyDataViewHolder(@NonNull View itemView) {
            super(itemView);
            cropName = itemView.findViewById(R.id.cropName);
            year = itemView.findViewById(R.id.year);
            tractorRentCost = itemView.findViewById(R.id.tractorRentCost);
            labourerCost = itemView.findViewById(R.id.labourerCost);
            fertilizerCost = itemView.findViewById(R.id.fertilizerCost);
            pesticideCost = itemView.findViewById(R.id.pesticideCost);
            harvestingCost = itemView.findViewById(R.id.harvestingCost);
            totalCost = itemView.findViewById(R.id.totalCost);
            profitOrLoss = itemView.findViewById(R.id.profitOrLoss);
        }
    }
}