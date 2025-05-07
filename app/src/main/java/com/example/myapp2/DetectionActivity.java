package com.example.myapp2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.*;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetectionActivity extends AppCompatActivity {
    private TextView motionTextView;
    private TextView timestampTextView;
    private Button automaticButton;
    private boolean isAutomatic = false;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        motionTextView = findViewById(R.id.motion_text_view);
        timestampTextView = findViewById(R.id.timestamp_text_view);
        automaticButton = findViewById(R.id.automatic_button);
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound); // Place alert_sound.mp3 in res/raw folder

        fetchMotionData();

        automaticButton.setOnClickListener(v -> {
            isAutomatic = !isAutomatic;
            automaticButton.setText(isAutomatic ? "Automatic: ON" : "Automatic: OFF");
            Toast.makeText(this, isAutomatic ? "Automatic Mode Enabled" : "Automatic Mode Disabled", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchMotionData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://myserver-1.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getMotionData().enqueue(new Callback<List<MotionResponse>>() { // Handle List<MotionResponse>
            @Override
            public void onResponse(Call<List<MotionResponse>> call, Response<List<MotionResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Get the latest motion data point
                    MotionResponse latestMotion = response.body().get(0); // Extract the first item from the list
                    String motion = latestMotion.getMotion();
                    String timestamp = formatDate(latestMotion.getTimestamp());

                    // Update UI
                    motionTextView.setText("Motion: " + motion);
                    timestampTextView.setText("Last Updated: " + timestamp);

                    // Play alert sound if motion is detected and automatic mode is enabled
                    if (motion.equals("MOTION_DETECTED")) {
                        playAlertSound();
                    }
                } else {
                    Toast.makeText(DetectionActivity.this, "No motion data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MotionResponse>> call, Throwable t) {
                Toast.makeText(DetectionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(String timestamp) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
            Date date = inputFormat.parse(timestamp);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return timestamp;
        }
    }

    private void playAlertSound() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}