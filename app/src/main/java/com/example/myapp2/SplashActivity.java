package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private LinearLayout cardsContainer;
    private Handler handler = new Handler();
    private int delay = 300; // Delay between each card

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView title = findViewById(R.id.title);
        TextView subtitle = findViewById(R.id.subtitle);
        TextView sectionHeader = findViewById(R.id.section_header);
        TextView footerText = findViewById(R.id.footer_text);
        ImageView logo = findViewById(R.id.splash_image);
        Button letsGoButton = findViewById(R.id.btn_lets_go);
        cardsContainer = findViewById(R.id.cards_container);

        // Fade-in animation
        Animation fadeIn = new android.view.animation.AlphaAnimation(0, 1);
        fadeIn.setDuration(800);

        // Scale animation
        Animation scaleAnim = new ScaleAnimation(
                0.8f, 1.0f, 0.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnim.setDuration(600);

        // Start animations
        animateView(title, fadeIn, 300);
        animateView(subtitle, fadeIn, 600);
        animateView(logo, scaleAnim, 900);

        // Add features with animation
        String[] titles = {
                "📡 Hardware Integration",
                "📷 Raspberry Pi Camera",
                "📊 Node-RED + MongoDB",
                "🤖 Crop Disease Detection",
                "💡 Remote LED Control",
                "🎵 Motion Detection Alarm",
                "💬 Farmer Support Chat",
                "💰 Market Price Updates",
                "📊 Farm Data Tracker"
        };

        String[] descriptions = {
                "ESP32 with DHT11, Soil Moisture, PIR Sensors",
                "Capture real-time plant images via USB camera server",
                "Visualize sensor data and store it using Node-RED & MongoDB",
                "AI model detects diseases like Black Gram, Cotton, Tomato",
                "Turn ON/OFF LED lights directly from mobile app",
                "Play alarm sound automatically when motion is detected",
                "Chat with support team and send images for expert advice",
                "Stay updated on current market prices for better yield profit",
                "Save crop name, year, tractor rent, labor cost, etc."
        };

        int[] colors = {
                0xFF43A047, // Green
                0xFF0288D1, // Blue
                0xFFFBC02D, // Yellow
                0xFFAB47BC, // Purple
                0xFFFFA726, // Orange
                0xFFE53935, // Red
                0xFF5E35B1, // Deep Purple
                0xFFFFEB3B, // Amber
                0xFFEC407A  // Pink
        };

        // Add cards with animation
        for (int i = 0; i < titles.length; i++) {
            final int index = i;
            handler.postDelayed(() -> addFeatureCard(titles[index], descriptions[index], colors[index]), delay * i);
        }

        // Show "Let's Go" after last card
        handler.postDelayed(() -> {
            letsGoButton.setVisibility(View.VISIBLE);
            letsGoButton.animate().alpha(1f).setDuration(500);
        }, delay * titles.length);

        // Footer fade-in
        handler.postDelayed(() -> footerText.animate().alpha(1f).setDuration(600), delay * (titles.length + 1));

        // Button click
        letsGoButton.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        });
    }

    private void animateView(View view, Animation anim, long startDelay) {
        handler.postDelayed(() -> view.startAnimation(anim), startDelay);
        view.animate().alpha(1f).setStartDelay(startDelay).setDuration(600).start();
    }

    private void addFeatureCard(String title, String description, int color) {
        View card = LayoutInflater.from(this).inflate(R.layout.feature_card, cardsContainer, false);
        TextView cardTitle = card.findViewById(R.id.card_title);
        TextView cardDesc = card.findViewById(R.id.card_description);
        View divider = card.findViewById(R.id.divider);

        cardTitle.setText(title);
        cardDesc.setText(description);
        divider.setBackgroundColor(color);

        cardsContainer.addView(card);
        card.setAlpha(0f);
        card.animate().alpha(1f).setDuration(400).start();

        // Icon pulse animation
        ImageView icon = card.findViewById(R.id.icon);
        ScaleAnimation pulse = new ScaleAnimation(
                1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setDuration(800);
        icon.startAnimation(pulse);
    }
}