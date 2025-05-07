/*package com.example.myapp2;

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
}*/
package com.example.myapp2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import okhttp3.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantHealthActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String CAMERA_SERVER_URL = "https://11ae-223-228-97-224.ngrok-free.app/capture";
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private ImageView imageView;
    private Button chooseImageButton, realImageButton, predictButton;
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
        realImageButton = findViewById(R.id.realImageButton);
        predictButton = findViewById(R.id.predict_button);
        resultTextView = findViewById(R.id.result_text_view);

        // Check permissions for Internet and Storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://leaf-detection-7xhc.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Set Click Listeners
        chooseImageButton.setOnClickListener(v -> openImagePicker());
        realImageButton.setOnClickListener(v -> captureRealImage());
        predictButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                performPrediction(selectedImageUri);
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, continue with the app
            } else {
                Toast.makeText(this, "Permissions are required to proceed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Open Image Picker for gallery selection
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result from Image Picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Glide.with(this).load(selectedImageUri).into(imageView);
            predictButton.setEnabled(true);
        }
    }

    // Capture real image from Raspberry Pi
    private void captureRealImage() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(CAMERA_SERVER_URL)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(PlantHealthActivity.this,
                        "Camera server error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        String imageUrl = jsonObject.getString("image_url");
                        downloadAndSetImage(imageUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(PlantHealthActivity.this,
                                "Error parsing response",
                                Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(PlantHealthActivity.this,
                            "Failed to capture image: " + response.code(),
                            Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Download and display the captured image
    private void downloadAndSetImage(String imageUrl) {
        String imageUrlWithCacheBust = imageUrl + "?t=" + System.currentTimeMillis(); // Cache busting

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(imageUrlWithCacheBust).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(PlantHealthActivity.this,
                        "Download failed: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body == null) {
                        runOnUiThread(() -> Toast.makeText(PlantHealthActivity.this,
                                "Empty image data",
                                Toast.LENGTH_SHORT).show());
                        return;
                    }

                    File imageFile = new File(getCacheDir(), "real_image.jpg");
                    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                        fos.write(body.bytes());
                        runOnUiThread(() -> {
                            selectedImageUri = Uri.fromFile(imageFile);
                            Glide.with(PlantHealthActivity.this)
                                    .load(selectedImageUri)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(imageView);
                            predictButton.setEnabled(true);
                            Toast.makeText(PlantHealthActivity.this,
                                    "Image captured successfully",
                                    Toast.LENGTH_SHORT).show();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(PlantHealthActivity.this,
                                "Failed to save image",
                                Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(PlantHealthActivity.this,
                            "Image download failed: " + response.code(),
                            Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Perform prediction using the selected or captured image
    private void performPrediction(Uri imageUri) {
        try {
            File imageFile = uriToFile(imageUri);  // Convert Uri to File

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", imageFile.getName(), requestBody);

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

    // Convert Uri to File
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