package com.example.myapp2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
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
    private List<String> uniqueDistricts = new ArrayList<>();
    private List<String> uniqueMarkets = new ArrayList<>();
    private PriceAdapter adapter;
    private ArrayAdapter<String> marketAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        districtSpinner = findViewById(R.id.districtSpinner);
        marketSpinner = findViewById(R.id.marketSpinner);
        recyclerView = findViewById(R.id.recyclerView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://market-app-1.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        fetchDistricts();

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = uniqueDistricts.get(position);
                fetchMarkets(selectedDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        findViewById(R.id.submitButton).setOnClickListener(v -> {
            String district = districtSpinner.getSelectedItem() != null ? districtSpinner.getSelectedItem().toString() : "";
            String market = marketSpinner.getSelectedItem() != null ? marketSpinner.getSelectedItem().toString() : "";

            if (!district.isEmpty() && !market.isEmpty()) {
                fetchAllPrices(district, market);
            } else {
                Toast.makeText(MarketActivity.this, "Please select both district and market.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDistricts() {
        apiService.getDistricts().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    uniqueDistricts.clear();
                    for (String district : response.body()) {
                        if (!uniqueDistricts.contains(district)) {
                            uniqueDistricts.add(district);
                        }
                    }

                    if (!uniqueDistricts.isEmpty()) {
                        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(MarketActivity.this,
                                android.R.layout.simple_spinner_item, uniqueDistricts);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtSpinner.setAdapter(districtAdapter);
                    } else {
                        Toast.makeText(MarketActivity.this, "No districts available.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MarketActivity.this, "Failed to fetch districts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(MarketActivity.this, "Error fetching districts: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMarkets(String district) {
        apiService.getMarketsByDistrict(district).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    uniqueMarkets.clear();
                    for (String market : response.body()) {
                        if (!uniqueMarkets.contains(market)) {
                            uniqueMarkets.add(market);
                        }
                    }

                    if (!uniqueMarkets.isEmpty()) {
                        marketAdapter = new ArrayAdapter<>(MarketActivity.this,
                                android.R.layout.simple_spinner_item, uniqueMarkets);
                        marketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        marketSpinner.setAdapter(marketAdapter);
                    } else {
                        marketSpinner.setAdapter(null);
                        Toast.makeText(MarketActivity.this, "No markets available.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MarketActivity.this, "Failed to fetch markets", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(MarketActivity.this, "Error fetching markets: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllPrices(String district, String market) {
        apiService.getPricesByFilters(district, market).enqueue(new Callback<List<Price1>>() {
            @Override
            public void onResponse(Call<List<Price1>> call, Response<List<Price1>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Price1> priceList = response.body();

                    if (!priceList.isEmpty()) {
                        adapter = new PriceAdapter(priceList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MarketActivity.this));
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(MarketActivity.this, "No data available for selected market.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MarketActivity.this, "No data available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Price1>> call, Throwable t) {
                Toast.makeText(MarketActivity.this, "Error fetching prices: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
