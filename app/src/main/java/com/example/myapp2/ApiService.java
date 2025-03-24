package com.example.myapp2;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {
    @GET("/api/crops")
    Call<List<Crop>> getCrops();

    @POST("/api/crops/add")
    Call<Void> addCrop(@Body Crop crop);

    @PUT("/api/crops/edit/{id}")
    Call<Void> updateCrop(@Path("id") String id, @Body Crop crop);

    @DELETE("/api/crops/delete/{id}")
    Call<Void> deleteCrop(@Path("id") String id);

    // Existing Tomato Disease Prediction
    @GET("/api/districts")
    Call<List<String>> getDistricts();

    @GET("/api/markets")
    Call<List<String>> getMarketsByDistrict(@Query("district") String district);

    @GET("/api/prices")
    Call<List<Price1>> getPricesByFilters(
            @Query("district") String district,
            @Query("market") String market
    );

    @Multipart
    @POST("/predict_disease")
    Call<PredictionResponse> predict(@Part MultipartBody.Part file);

    // Cotton Disease Prediction (new name)
    @Multipart
    @POST("/predict_cotton")
    Call<CottonDiseaseResponse> predictCottonDisease(@Part MultipartBody.Part file);

    @Multipart
    @POST("/predict_blackgram") // Replace with your backend endpoint
    Call<BlackGramDiseaseResponse> predictBlackGramDisease(@Part MultipartBody.Part file);

    @Multipart
    @POST("/predict_sunflower") // New endpoint for sunflower
    Call<SunflowerDiseaseResponse> predictSunflowerDisease(@Part MultipartBody.Part file);

    // Other endpoints remain unchanged
    @POST("/client/send-message")
    Call<Void> sendMessage(@Body Message message);

    @GET("/client/get-messages")
    Call<List<Message>> getMessages();

    @GET("/motion")
    Call<List<MotionResponse>> getMotionData();

    @GET("/digisoil")
    Call<List<SoilMoistureResponse>> getSoilMoistureData();
}
