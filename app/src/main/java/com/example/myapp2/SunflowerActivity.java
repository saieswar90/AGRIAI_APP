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

public class SunflowerActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private TextView resultTextView;
    private Uri selectedImageUri;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunflower); // New layout file

        // Initialize Views
        imageView = findViewById(R.id.imageView);
        Button chooseImageButton = findViewById(R.id.chooseImageButton);
        Button predictButton = findViewById(R.id.predictButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-render-user-url/") // Replace with your Render URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Button Listeners
        chooseImageButton.setOnClickListener(v -> openImagePicker());
        predictButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                performPrediction(selectedImageUri);
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Glide.with(this).load(selectedImageUri).into(imageView);
        }
    }

    private void performPrediction(Uri imageUri) {
        try {
            File imageFile = uriToFile(imageUri);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);

            // Use the sunflower-specific endpoint
            apiService.predictSunflowerDisease(imagePart).enqueue(new Callback<SunflowerDiseaseResponse>() {
                @Override
                public void onResponse(Call<SunflowerDiseaseResponse> call, Response<SunflowerDiseaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        SunflowerDiseaseResponse prediction = response.body();
                        String result = "Prediction: " + prediction.getPrediction() +
                                "\nConfidence: " + (prediction.getConfidence() * 100) + "%" +
                                "\nSuggestion: " + prediction.getSuggestion();
                        resultTextView.setText(result);
                    }
                }

                @Override
                public void onFailure(Call<SunflowerDiseaseResponse> call, Throwable t) {
                    Toast.makeText(SunflowerActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

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