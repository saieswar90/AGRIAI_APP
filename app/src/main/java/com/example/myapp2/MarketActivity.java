package com.example.myapp2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarketActivity extends AppCompatActivity {

    private Spinner districtSpinner, marketSpinner;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private List<String> districts = new ArrayList<>();
    private List<String> markets = new ArrayList<>();
    private PriceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        // Initialize views
        districtSpinner = findViewById(R.id.districtSpinner);
        marketSpinner = findViewById(R.id.marketSpinner);
        recyclerView = findViewById(R.id.recyclerView);

        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://market-app-1.onrender.com/") // Replace with your Host URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        fetchDistricts();

        // District spinner listener
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = districts.get(position);
                fetchMarkets(selectedDistrict);  // Fetch markets based on the selected district
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });

        // Submit button listener
        findViewById(R.id.submitButton).setOnClickListener(v -> {
            String district = districtSpinner.getSelectedItem().toString();
            String market = marketSpinner.getSelectedItem().toString();
            fetchAllPrices(district, market);  // Fetch prices based on district and market
        });
    }

    // Fetch districts from the API
    private void fetchDistricts() {
        apiService.getDistricts().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> allDistricts = response.body();

                    // Normalize and remove duplicates using HashSet (case-insensitive and trimming whitespace)
                    HashSet<String> uniqueDistrictsSet = new HashSet<>();
                    for (String district : allDistricts) {
                        if (district != null) {
                            uniqueDistrictsSet.add(district.trim().toLowerCase());
                        }
                    }
                    List<String> uniqueDistricts = new ArrayList<>(uniqueDistrictsSet);

                    // Check if there are duplicates
                    if (uniqueDistricts.size() < allDistricts.size()) {

                    }

                    // Set adapter for the district spinner
                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(MarketActivity.this,
                            android.R.layout.simple_spinner_item, uniqueDistricts);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    districtSpinner.setAdapter(districtAdapter);

                    // Store unique districts in the list for further processing
                    districts = uniqueDistricts;

                    // Optionally set the default selection if necessary
                    districtSpinner.setSelection(0);  // Select the first item as default
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(MarketActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Fetch markets based on the selected district
    private void fetchMarkets(String district) {
        apiService.getMarketsByDistrict(district).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> allMarkets = response.body();

                    // Removing duplicates using HashSet and ensuring case-insensitivity
                    HashSet<String> uniqueMarketsSet = new HashSet<>(allMarkets);
                    List<String> uniqueMarkets = new ArrayList<>(uniqueMarketsSet);

                    // Check if there are duplicates
                    if (uniqueMarkets.size() < allMarkets.size()) {
                        Toast.makeText(MarketActivity.this, "Duplicate entries removed", Toast.LENGTH_SHORT).show();
                    }

                    // Set adapter for the market spinner
                    ArrayAdapter<String> marketAdapter = new ArrayAdapter<>(MarketActivity.this,
                            android.R.layout.simple_spinner_item, uniqueMarkets);
                    marketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    marketSpinner.setAdapter(marketAdapter);

                    // Store unique markets in the list for further processing
                    markets = uniqueMarkets;

                    // Optionally set the default selection if necessary
                    marketSpinner.setSelection(0);  // Select the first item as default
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(MarketActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch prices based on district and market selection
    private void fetchAllPrices(String district, String market) {
        apiService.getPricesByFilters(district, market).enqueue(new Callback<List<Price1>>() {
            @Override
            public void onResponse(Call<List<Price1>> call, Response<List<Price1>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new PriceAdapter(response.body());
                    recyclerView.setLayoutManager(new LinearLayoutManager(MarketActivity.this));
                    recyclerView.setAdapter(adapter); // Set adapter for RecyclerView
                }
            }

            @Override
            public void onFailure(Call<List<Price1>> call, Throwable t) {
                Toast.makeText(MarketActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
