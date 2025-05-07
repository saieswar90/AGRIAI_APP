package com.example.myapp2;

import com.google.gson.annotations.SerializedName;

public class Price1 {
    @SerializedName("_id")
    private String id;
    private String state;
    private String district;
    private String market;
    private String commodity;
    private String variety;
    private double maxPrice;
    private double avgPrice;
    private double minPrice;

    public String getCommodity() { return commodity; }
    public String getVariety() { return variety; }
    public double getMaxPrice() { return maxPrice; }
    public double getAvgPrice() { return avgPrice; }
    public double getMinPrice() { return minPrice; }
}
