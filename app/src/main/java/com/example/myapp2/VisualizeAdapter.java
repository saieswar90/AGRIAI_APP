package com.example.myapp2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import com.github.mikephil.charting.components.Legend;

import java.util.List;

public class VisualizeAdapter extends RecyclerView.Adapter<VisualizeAdapter.VisualizeViewHolder> {

    private List<Crop> crops;

    public VisualizeAdapter(List<Crop> crops) {
        this.crops = crops;
    }

    @NonNull
    @Override
    public VisualizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_visualize, parent, false);
        return new VisualizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisualizeViewHolder holder, int position) {
        Crop crop = crops.get(position);

        // Set crop name and year
        holder.cropName.setText(crop.getCropName());
        holder.year.setText(String.valueOf(crop.getYear()));

        // Setup pie chart entries
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) crop.getTractorRentCost(), "Tractor Rent"));
        entries.add(new PieEntry((float) crop.getLabourerCost(), "Labourer Cost"));
        entries.add(new PieEntry((float) crop.getFertilizerCost(), "Fertilizer Cost"));
        entries.add(new PieEntry((float) crop.getPesticideCost(), "Pesticide Cost"));
        entries.add(new PieEntry((float) crop.getHarvestingCost(), "Harvesting Cost"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA);
        dataSet.setValueTextColor(Color.BLACK); // Ensure label text is black
        dataSet.setValueTextSize(12f);

        // Enable value lines (connecting lines)
        dataSet.setValueLinePart1Length(0.5f);  // Length of first part of the line
        dataSet.setValueLinePart2Length(0.4f);  // Length of second part of the line
        dataSet.setValueLineWidth(2f);  // Thickness of the line
        dataSet.setValueTextColor(Color.BLACK); // Ensure text is black
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // Labels outside the pie chart

        PieData pieData = new PieData(dataSet);
        holder.pieChart.setData(pieData);

        // Configure chart appearance
        holder.pieChart.setDrawEntryLabels(false); // Remove inside labels
        holder.pieChart.getDescription().setEnabled(false); // Hide description
        holder.pieChart.setExtraOffsets(10, 10, 10, 10); // Prevent cropping

        // Enable legend for better readability
        Legend legend = holder.pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.BLACK); // Black legend text
        legend.setWordWrapEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        holder.pieChart.invalidate(); // Refresh chart

        // Calculate total cost and profit/loss
        double totalCost = crop.getTractorRentCost() + crop.getLabourerCost() +
                crop.getFertilizerCost() + crop.getPesticideCost() + crop.getHarvestingCost();
        double profitOrLoss = crop.getAmountSold() - totalCost;

        holder.totalCost.setText(String.format("Total Cost: ₹%.2f", totalCost));
        holder.profitOrLoss.setText(profitOrLoss >= 0 ?
                String.format("Profit: ₹%.2f", profitOrLoss) :
                String.format("Loss: ₹%.2f", Math.abs(profitOrLoss)));
    }

    @Override
    public int getItemCount() {
        return crops.size();
    }

    static class VisualizeViewHolder extends RecyclerView.ViewHolder {
        TextView cropName, year, totalCost, profitOrLoss;
        PieChart pieChart;

        public VisualizeViewHolder(@NonNull View itemView) {
            super(itemView);
            cropName = itemView.findViewById(R.id.cropName);
            year = itemView.findViewById(R.id.year);
            pieChart = itemView.findViewById(R.id.pieChart);
            totalCost = itemView.findViewById(R.id.totalCost);
            profitOrLoss = itemView.findViewById(R.id.profitOrLoss);
        }
    }
}