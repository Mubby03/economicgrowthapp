package com.example.economicgrowthapp.landingpage;

public class GdpDataModel {
    private String title;
    private String amount;
    private String growthPercentage;
    private String revenueText;

    public GdpDataModel(String title, String amount, String growthPercentage, String revenueText) {
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

    public void setGrowthPercentage(String growthPercentage) {
        this.growthPercentage = growthPercentage;
    }

    public void setRevenueText(String revenueText) {
        this.revenueText = revenueText;
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


    public String getRevenueText() {
        return revenueText;
    }


    // Constructor, getters, and setters
    // Similar to InflationDataModel class
}
