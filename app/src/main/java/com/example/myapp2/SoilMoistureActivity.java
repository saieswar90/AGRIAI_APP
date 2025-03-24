package com.example.myapp2;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SoilMoistureActivity extends AppCompatActivity {
    private TextView soilMoistureTextView;
    private TextView soilConditionTextView; // New TextView for soil condition
    private TextView dateTextView; // New TextView for current date and time
    private ImageView soilMoistureIcon;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_moisture);

        // Initialize Views
        soilMoistureTextView = findViewById(R.id.soil_moisture_text_view);
        soilConditionTextView = findViewById(R.id.soil_condition_text_view); // Initialize new TextView
        dateTextView = findViewById(R.id.date_text_view); // Initialize date TextView
        soilMoistureIcon = findViewById(R.id.soil_moisture_icon);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://myserver-1.onrender.com/") // Base URL of your server
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Fetch soil moisture data
        fetchSoilMoistureData();

        // Start updating the current date and time
        updateDateTime();
    }

    private void fetchSoilMoistureData() {
        apiService.getSoilMoistureData().enqueue(new Callback<List<SoilMoistureResponse>>() {
            @Override
            public void onResponse(Call<List<SoilMoistureResponse>> call, Response<List<SoilMoistureResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SoilMoistureResponse> soilMoistureList = response.body();

                    if (!soilMoistureList.isEmpty()) {
                        // Get the latest soil moisture data
                        SoilMoistureResponse latestData = soilMoistureList.get(0);

                        // Extract soil moisture value
                        int soilMoisture = latestData.getSoilMoisture();

                        // Update UI
                        soilMoistureTextView.setText("Soil Moisture: " + soilMoisture);

                        // Determine soil condition
                        String soilCondition;
                        if (soilMoisture < 2000) {
                            soilCondition = "Soil is Wet!";
                            soilMoistureIcon.setImageResource(R.drawable.ic_moderate); // Wet soil icon
                        } else if (soilMoisture >= 2000 && soilMoisture <= 4000) {
                            soilCondition = "Soil is Moderate.";
                            soilMoistureIcon.setImageResource(R.drawable.ic_moderate); // Moderate soil icon
                        } else {
                            soilCondition = "Soil is Dry! Needs Watering.";
                            soilMoistureIcon.setImageResource(R.drawable.ic_moderate); // Dry soil icon
                        }

                        // Update soil condition TextView
                        soilConditionTextView.setText(soilCondition);
                    } else {
                        Toast.makeText(SoilMoistureActivity.this, "No soil moisture data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SoilMoistureActivity.this, "Failed to fetch soil moisture data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SoilMoistureResponse>> call, Throwable t) {
                Toast.makeText(SoilMoistureActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDateTime() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String pattern = "EEE, MMM dd, yyyy hh:mm a";
                String currentDateTime = new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
                dateTextView.setText(currentDateTime);
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(runnable); // Start the runnable
    }
}
