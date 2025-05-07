package com.example.myapp2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MotionDetectionService extends Service {
    private static final String CHANNEL_ID = "MotionDetectionServiceChannel";
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable fetchRunnable;
    private static final long FETCH_INTERVAL = 10000; // 10 seconds

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification());

        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound);
        handler = new Handler();
        startRepeatingFetch();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void startRepeatingFetch() {
        fetchRunnable = new Runnable() {
            @Override
            public void run() {
                fetchMotionData(); // Fetch motion data
                handler.postDelayed(this, FETCH_INTERVAL); // Repeat every 10 seconds
            }
        };
        handler.post(fetchRunnable); // Start the first fetch
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
                    MotionResponse latestMotion = response.body().get(0);
                    String motion = latestMotion.getMotion();
                    Log.d("MotionDetection", "Motion: " + motion);

                    if ("NO_MOTION".equals(motion)) {
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
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Motion Detection Service")
                .setContentText("Monitoring motion in the background")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Motion Detection Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Running in the background");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && fetchRunnable != null) {
            handler.removeCallbacks(fetchRunnable);
        }
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
