package com.example.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class LayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        // Tomato Container
        LinearLayout tomatoContainer = findViewById(R.id.tomato_container);
        tomatoContainer.setOnClickListener(v -> {
            Intent intent = new Intent(LayoutActivity.this, PlantHealthActivity.class);
            startActivity(intent);
        });

        // Cotton Container
        LinearLayout cottonContainer = findViewById(R.id.cotton_container);
        cottonContainer.setOnClickListener(v -> {
            Intent intent = new Intent(LayoutActivity.this, CottonActivity.class);
            startActivity(intent);
        });



        // Black Gram Container (New)
        LinearLayout blackGramContainer = findViewById(R.id.black_gram_container);
        blackGramContainer.setOnClickListener(v -> {
            Intent intent = new Intent(LayoutActivity.this, BlackGramActivity.class);
            startActivity(intent);
        });
    }
}