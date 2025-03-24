package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {
    private GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        gifImageView = findViewById(R.id.gifImageView);

        // Delay for 2.5 seconds, then start fade-out animation
        new Handler().postDelayed(() -> {
            fadeOutAndStartMainActivity();
        }, 2500);
    }

    private void fadeOutAndStartMainActivity() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(500); // 0.5 second fade-out
        fadeOut.setFillAfter(true);

        gifImageView.startAnimation(fadeOut);

        // Start MainActivity after animation completes
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 500);
    }
}
