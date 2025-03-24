package com.example.myapp2;

import com.google.gson.annotations.SerializedName;

public class Crop {
    @SerializedName("_id")
    private String id;
    private String cropName;
    private int year;
    private double tractorRentCost;
    private double labourerCost;
    private double fertilizerCost;
    private double pesticideCost;
    private double harvestingCost;
    private double amountSold;

    // Default Constructor
    public Crop() {}

    // Parameterized Constructor
    public Crop(String cropName, int year, double tractorRentCost, double labourerCost,
                double fertilizerCost, double pesticideCost, double harvestingCost, double amountSold) {
        this.cropName = cropName;
        this.year = year;
        this.tractorRentCost = tractorRentCost;
        this.labourerCost = labourerCost;
        this.fertilizerCost = fertilizerCost;
        this.pesticideCost = pesticideCost;
        this.harvestingCost = harvestingCost;
        this.amountSold = amountSold;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getTractorRentCost() { return tractorRentCost; }
    public void setTractorRentCost(double tractorRentCost) { this.tractorRentCost = tractorRentCost; }

    public double getLabourerCost() { return labourerCost; }
    public void setLabourerCost(double labourerCost) { this.labourerCost = labourerCost; }

    public double getFertilizerCost() { return fertilizerCost; }
    public void setFertilizerCost(double fertilizerCost) { this.fertilizerCost = fertilizerCost; }

    public double getPesticideCost() { return pesticideCost; }
    public void setPesticideCost(double pesticideCost) { this.pesticideCost = pesticideCost; }

    public double getHarvestingCost() { return harvestingCost; }
    public void setHarvestingCost(double harvestingCost) { this.harvestingCost = harvestingCost; }

    public double getAmountSold() { return amountSold; }
    public void setAmountSold(double amountSold) { this.amountSold = amountSold; }
}