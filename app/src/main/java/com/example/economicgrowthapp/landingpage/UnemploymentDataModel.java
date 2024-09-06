package com.example.economicgrowthapp.landingpage;

public class UnemploymentDataModel {
    private String title;
    private String amount;
    private String growthPercentage;
    private String revenueText;

    // Constructor, getters, and setters
    // Similar to InflationDataModel class

    public UnemploymentDataModel(String title, String amount, String growthPercentage, String revenueText) {
        this.title = title;
        this.amount = amount;
        this.growthPercentage = growthPercentage;
        this.revenueText = revenueText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getGrowthPercentage() {
        return growthPercentage;
    }

    public void setGrowthPercentage(String growthPercentage) {
        this.growthPercentage = growthPercentage;
    }

    public String getRevenueText() {
        return revenueText;
    }

    public void setRevenueText(String revenueText) {
        this.revenueText = revenueText;
    }
}
