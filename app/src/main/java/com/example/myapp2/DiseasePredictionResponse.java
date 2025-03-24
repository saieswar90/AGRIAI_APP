
package com.example.myapp2;

class DiseasePredictionResponse {
    private String prediction;
    private float confidence;
    private String suggestion;

    public String getPrediction() {
        return prediction;
    }

    public float getConfidence() {
        return confidence;
    }

    public String getSuggestion() {
        return suggestion;
    }
}