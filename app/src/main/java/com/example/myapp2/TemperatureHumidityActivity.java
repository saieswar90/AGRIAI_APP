package com.example.myapp2;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TemperatureHumidityActivity extends AppCompatActivity {

    private OkHttpClient client;
    private TextView dateTextView, temperatureTextView, humidityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_humidity);

        // Initialize views
        dateTextView = findViewById(R.id.date_text_view);
        temperatureTextView = findViewById(R.id.temperature_text_view);
        humidityTextView = findViewById(R.id.humidity_text_view);

        // Update date and time dynamically
        updateDateTime();

        // Fetch data from the API
        client = new OkHttpClient();
        fetchDataFromAPI();
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
        handler.post(runnable);
    }

    private void fetchDataFromAPI() {
        Request request = new Request.Builder()
                .url("https://myserver-1.onrender.com/climate")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(TemperatureHumidityActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    List<ClimateData> climateDataList = new Gson().fromJson(responseData, new TypeToken<List<ClimateData>>() {}.getType());

                    if (climateDataList != null && !climateDataList.isEmpty()) {
                        ClimateData latestData = climateDataList.get(climateDataList.size() - 1); // Get the latest data

                        runOnUiThread(() -> {
                            temperatureTextView.setText(String.format(Locale.getDefault(), "%.1f °C", latestData.temperature));
                            humidityTextView.setText(String.format(Locale.getDefault(), "%.1f %%", latestData.humidity));
                        });
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(TemperatureHumidityActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Model class for JSON parsing
    private static class ClimateData {
        String _id;
        double temperature;
        double humidity;
        String timestamp;
    }
}