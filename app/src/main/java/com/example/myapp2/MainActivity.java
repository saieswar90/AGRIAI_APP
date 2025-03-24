package com.example.myapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Update Date, Time, and Day
        TextView dateTimeDay = findViewById(R.id.date_time_day);
        updateDateTime(dateTimeDay);

        // Handle Navigation Buttons
        Button temperatureButton = findViewById(R.id.temperature_button);
        Button soilMoistureButton = findViewById(R.id.soil_moisture_button);
        Button detectionButton = findViewById(R.id.detection_button);
        Button startServiceButton = findViewById(R.id.start_service_button);
        Button stopServiceButton = findViewById(R.id.stop_service_button);

        temperatureButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TemperatureHumidityActivity.class);
            startActivity(intent);
        });
        soilMoistureButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SoilMoistureActivity.class);
            startActivity(intent);
        });
        detectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetectionActivity.class);
            startActivity(intent);
        });

        startServiceButton.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(MainActivity.this, MotionDetectionService.class);
            serviceIntent.putExtra("isAutomatic", false); // Manual mode by default
            startForegroundService(serviceIntent);
        });

        // Stop Service Button
        stopServiceButton.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(MainActivity.this, MotionDetectionService.class);
            stopService(serviceIntent);
        });
        // Handle Container Clicks
        LinearLayout plantHealthContainer = findViewById(R.id.plant_health_container);
        LinearLayout helpContainer = findViewById(R.id.help_container);
        LinearLayout ledControlContainer = findViewById(R.id.led_control_container);
        LinearLayout dashboardContainer = findViewById(R.id.dashboard_container);
        LinearLayout marketContainer=findViewById(R.id.market_container);
        LinearLayout dataContainer=findViewById(R.id.data_container);

        plantHealthContainer.setOnClickListener(v ->
            {
                Intent intent = new Intent(MainActivity.this, LayoutActivity.class);
                startActivity(intent);
        });
        dataContainer.setOnClickListener(v->
                {
                    Intent intent=new Intent(MainActivity.this,DetailActivity.class);
                    startActivity(intent);
                });
        marketContainer.setOnClickListener(v->
        {
            Intent intent=new Intent(MainActivity.this,MarketActivity.class);
            startActivity(intent);
        });
        helpContainer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);

        });
        ledControlContainer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LEDControlActivity.class);
            startActivity(intent);
        });
        dashboardContainer.setOnClickListener(v -> openUrl("https://fc7a-61-1-167-250.ngrok-free.app/ui"));
    }

    private void updateDateTime(TextView dateTimeDay) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String pattern = "EEE, MMM dd, yyyy hh:mm a";
                String currentDateTime = new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
                dateTimeDay.setText(currentDateTime);
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(runnable);
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}