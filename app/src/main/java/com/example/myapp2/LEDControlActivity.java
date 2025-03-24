package com.example.myapp2;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LEDControlActivity extends AppCompatActivity {

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);

        // Initialize OkHttpClient
        client = new OkHttpClient();

        // Find buttons
        Button redButton = findViewById(R.id.red_button);
        Button greenButton = findViewById(R.id.green_button);
        Button yellowButton = findViewById(R.id.yellow_button);
        Button automaticButton = findViewById(R.id.automatic_button);

        // Set click listeners for each button
        redButton.setOnClickListener(v -> toggleLED("https://b095-61-1-167-250.ngrok-free.app/toggleRed"));
        greenButton.setOnClickListener(v -> toggleLED("https://b095-61-1-167-250.ngrok-free.app/toggleGreen"));
        yellowButton.setOnClickListener(v -> toggleLED("https://b095-61-1-167-250.ngrok-free.app/toggleYellow"));
        automaticButton.setOnClickListener(v -> toggleLED("https://b095-61-1-167-250.ngrok-free.app/toggleAuto"));

    }

    private void toggleLED(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(LEDControlActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(LEDControlActivity.this, "LED toggled successfully", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(LEDControlActivity.this, "Failed to toggle LED", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}