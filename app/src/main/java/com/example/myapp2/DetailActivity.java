package com.example.myapp2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button visualizeButton, myDataButton, editButton, addButton, deleteButton;
    private ApiService apiService;
    private EditAdapter adapter;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        visualizeButton = findViewById(R.id.visualizeButton);
        myDataButton = findViewById(R.id.myDataButton);
        editButton = findViewById(R.id.editButton);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Initially hide Add and Delete buttons
        addButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mydata-server.onrender.com/") // Replace with your actual backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Button listeners
        visualizeButton.setOnClickListener(v -> loadVisualizePage());
        myDataButton.setOnClickListener(v -> loadMyDataPage());
        editButton.setOnClickListener(v -> toggleEditMode());
        addButton.setOnClickListener(v -> showAddDialog());
        deleteButton.setOnClickListener(v -> deleteSelectedRecords());
    }

    private void loadVisualizePage() {
        isEditMode = false;
        hideEditModeButtons();
        apiService.getCrops().enqueue(new Callback<List<Crop>>() {
            @Override
            public void onResponse(Call<List<Crop>> call, Response<List<Crop>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VisualizeAdapter adapter = new VisualizeAdapter(response.body());
                    recyclerView.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Crop>> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMyDataPage() {
        isEditMode = false;
        hideEditModeButtons();
        apiService.getCrops().enqueue(new Callback<List<Crop>>() {
            @Override
            public void onResponse(Call<List<Crop>> call, Response<List<Crop>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyDataAdapter adapter = new MyDataAdapter(response.body());
                    recyclerView.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Crop>> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        if (isEditMode) {
            // Show Add and Delete buttons
            addButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);

            // Load Edit Mode data
            apiService.getCrops().enqueue(new Callback<List<Crop>>() {
                @Override
                public void onResponse(Call<List<Crop>> call, Response<List<Crop>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        adapter = new EditAdapter(response.body(), DetailActivity.this, apiService);
                        recyclerView.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<List<Crop>> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Hide Add and Delete buttons
            hideEditModeButtons();
            loadMyDataPage(); // Return to default view
        }
    }

    private void hideEditModeButtons() {
        addButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_crop, null);
        builder.setView(view);

        EditText cropName = view.findViewById(R.id.cropName);
        EditText year = view.findViewById(R.id.year);
        EditText tractorRentCost = view.findViewById(R.id.tractorRentCost);
        EditText labourerCost = view.findViewById(R.id.labourerCost);
        EditText fertilizerCost = view.findViewById(R.id.fertilizerCost);
        EditText pesticideCost = view.findViewById(R.id.pesticideCost);
        EditText harvestingCost = view.findViewById(R.id.harvestingCost);
        EditText amountSold = view.findViewById(R.id.amountSold);

        builder.setPositiveButton("Add", (dialog, which) -> {
            Crop newCrop = new Crop(
                    cropName.getText().toString(),
                    Integer.parseInt(year.getText().toString()),
                    Double.parseDouble(tractorRentCost.getText().toString()),
                    Double.parseDouble(labourerCost.getText().toString()),
                    Double.parseDouble(fertilizerCost.getText().toString()),
                    Double.parseDouble(pesticideCost.getText().toString()),
                    Double.parseDouble(harvestingCost.getText().toString()),
                    Double.parseDouble(amountSold.getText().toString())
            );
            addCrop(newCrop);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addCrop(Crop crop) {
        apiService.addCrop(crop).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, "Record added successfully", Toast.LENGTH_SHORT).show();
                    toggleEditMode(); // Refresh Edit Mode
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSelectedRecords() {
        if (adapter != null) {
            List<String> selectedIds = adapter.getSelectedIds();
            if (selectedIds.isEmpty()) {
                Toast.makeText(this, "No records selected", Toast.LENGTH_SHORT).show();
                return;
            }
            for (String id : selectedIds) {
                apiService.deleteCrop(id).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(DetailActivity.this, "Record deleted successfully", Toast.LENGTH_SHORT).show();
                            toggleEditMode(); // Refresh Edit Mode
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void showEditDeleteDialog(Crop crop) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new CharSequence[]{"Edit", "Delete"}, (dialog, which) -> {
            if (which == 0) {
                // Edit option
                showEditDialog(crop);
            } else {
                // Delete option
                deleteCrop(crop.getId());
            }
        });
        builder.show();
    }

    private void showEditDialog(Crop crop) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_crop, null);
        builder.setView(view);

        TextView cropNameLabel = view.findViewById(R.id.cropNameLabel);
        TextView yearLabel = view.findViewById(R.id.yearLabel);
        EditText tractorRentCost = view.findViewById(R.id.tractorRentCost);
        EditText labourerCost = view.findViewById(R.id.labourerCost);
        EditText fertilizerCost = view.findViewById(R.id.fertilizerCost);
        EditText pesticideCost = view.findViewById(R.id.pesticideCost);
        EditText harvestingCost = view.findViewById(R.id.harvestingCost);
        EditText amountSold = view.findViewById(R.id.amountSold);

        cropNameLabel.setText("Crop Name: " + crop.getCropName());
        yearLabel.setText("Year: " + crop.getYear());
        tractorRentCost.setText(String.valueOf(crop.getTractorRentCost()));
        labourerCost.setText(String.valueOf(crop.getLabourerCost()));
        fertilizerCost.setText(String.valueOf(crop.getFertilizerCost()));
        pesticideCost.setText(String.valueOf(crop.getPesticideCost()));
        harvestingCost.setText(String.valueOf(crop.getHarvestingCost()));
        amountSold.setText(String.valueOf(crop.getAmountSold()));

        builder.setPositiveButton("Update", (dialog, which) -> {
            crop.setTractorRentCost(Double.parseDouble(tractorRentCost.getText().toString()));
            crop.setLabourerCost(Double.parseDouble(labourerCost.getText().toString()));
            crop.setFertilizerCost(Double.parseDouble(fertilizerCost.getText().toString()));
            crop.setPesticideCost(Double.parseDouble(pesticideCost.getText().toString()));
            crop.setHarvestingCost(Double.parseDouble(harvestingCost.getText().toString()));
            crop.setAmountSold(Double.parseDouble(amountSold.getText().toString()));
            updateCrop(crop);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateCrop(Crop crop) {
        apiService.updateCrop(crop.getId(), crop).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, "Record updated successfully", Toast.LENGTH_SHORT).show();
                    toggleEditMode(); // Refresh Edit Mode
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCrop(String id) {
        apiService.deleteCrop(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, "Record deleted successfully", Toast.LENGTH_SHORT).show();
                    toggleEditMode(); // Refresh Edit Mode
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}