package com.example.myapp2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import java.util.*;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MotionDetectionService extends Service {
    private static final String CHANNEL_ID = "MotionDetectionServiceChannel";
    private boolean isAutomatic = false;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(); // Create the notification channel
        startForeground(1, createNotification()); // Start the foreground service
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound); // Place alert_sound.mp3 in res/raw folder
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            isAutomatic = intent.getBooleanExtra("isAutomatic", false);
        }

        fetchMotionData();
        return START_STICKY; // Ensures the service restarts if killed by the system
    }

    private void fetchMotionData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://myserver-1.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getMotionData().enqueue(new Callback<List<MotionResponse>>() {
            @Override
            public void onResponse(Call<List<MotionResponse>> call, Response<List<MotionResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    MotionResponse latestMotion = response.body().get(0); // Get the latest data point
                    String motion = latestMotion.getMotion();
                    Log.d("MotionDetection", "Motion: " + motion);

                    if (motion.equals("MOTION_DETECTED") && isAutomatic) {
                        playAlertSound();
                    }
                } else {
                    Log.e("MotionDetection", "No motion data found");
                }
            }

            @Override
            public void onFailure(Call<List<MotionResponse>> call, Throwable t) {
                Log.e("MotionDetection", "Error fetching motion data: " + t.getMessage());
            }
        });
    }

    private void playAlertSound() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private Notification createNotification() {
        String channelId = "MotionDetectionServiceChannel"; // Must match the channel ID

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Motion Detection Service")
                .setContentText("Running in the background")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
                .setPriority(NotificationCompat.PRIORITY_LOW); // Low priority for minimal disruption

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0+
            String channelId = "MotionDetectionServiceChannel";
            String channelName = "Motion Detection Service";
            String channelDescription = "Running in the background";

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(channelDescription);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}