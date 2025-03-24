package com.example.myapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.bumptech.glide.Glide;

public class PlantHealthActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private Button chooseImageButton, predictButton;
    private TextView resultTextView;
    private Uri selectedImageUri;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_health);

        // Initialize Views
        imageView = findViewById(R.id.image_view);
        chooseImageButton = findViewById(R.id.choose_button);
        predictButton = findViewById(R.id.predict_button);
        resultTextView = findViewById(R.id.result_text_view);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://leaf-detection-7xhc.onrender.com/") // Replace with your server URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Set OnClickListener for "Choose Image" Button
        chooseImageButton.setOnClickListener(v -> openImagePicker());

        // Set OnClickListener for "Predict" Button
        predictButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                performPrediction(selectedImageUri);
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Open Image Picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle Image Selection Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Glide.with(this).load(selectedImageUri).into(imageView); // Display the selected image
            predictButton.setEnabled(true);
        }
    }

    // Perform Prediction by Sending Image to Backend
    private void performPrediction(Uri imageUri) {
        try {
            // Convert URI to File
            File imageFile = uriToFile(imageUri);

            // Create RequestBody and MultipartBody
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);

            // Send request to backend
            apiService.predict(imagePart).enqueue(new Callback<PredictionResponse>() {
                @Override
                public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PredictionResponse prediction = response.body();
                        String result = "Prediction: " + prediction.getPrediction() +
                                "\nConfidence: " + (prediction.getConfidence() * 100) + "%" +
                                "\nSuggestion: " + prediction.getSuggestion();

                        resultTextView.setText(result);
                    } else {
                        Toast.makeText(PlantHealthActivity.this, "Failed to get prediction", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PredictionResponse> call, Throwable t) {
                    Toast.makeText(PlantHealthActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to convert URI to File
    private File uriToFile(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File file = new File(getCacheDir(), "temp_image.jpg");
        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }
}