package com.example.myapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.EditViewHolder> {
    private List<Crop> crops;
    private Context context;
    private ApiService apiService;
    private List<String> selectedIds;

    public EditAdapter(List<Crop> crops, Context context, ApiService apiService) {
        this.crops = crops;
        this.context = context;
        this.apiService = apiService;
        this.selectedIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public EditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit, parent, false);
        return new EditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditViewHolder holder, int position) {
        Crop crop = crops.get(position);

        // Bind data to views
        holder.cropName.setText(crop.getCropName());
        holder.year.setText(String.valueOf(crop.getYear()));

        // Reset checkbox listener to avoid conflicts
        holder.checkBox.setOnCheckedChangeListener(null);

        // Set checkbox state based on selection
        holder.checkBox.setChecked(selectedIds.contains(crop.getId()));

        // Add or remove ID from selectedIds based on checkbox state
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedIds.add(crop.getId());
            } else {
                selectedIds.remove(crop.getId());
            }
        });

        // Open Edit/Delete dialog when item is clicked
        holder.itemView.setOnClickListener(v -> {
            ((DetailActivity) context).showEditDeleteDialog(crop);
        });
    }

    @Override
    public int getItemCount() {
        return crops.size();
    }

    public List<String> getSelectedIds() {
        return selectedIds;
    }

    static class EditViewHolder extends RecyclerView.ViewHolder {
        TextView cropName, year;
        CheckBox checkBox;

        public EditViewHolder(@NonNull View itemView) {
            super(itemView);
            cropName = itemView.findViewById(R.id.cropName);
            year = itemView.findViewById(R.id.year);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}