// Indicator.java
package com.example.economicgrowthapp.landingpage.fragments;

import java.util.List;

public class Indicator {
    private String indicatorName;
    private String indicatorName2;
    private String indicatorNumber;
    private String indicatorGrowthRate;
    private List<String> xAxis;
    private List<String> yAxis;

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getIndicatorName2() {
        return indicatorName2;
    }

    public void setIndicatorName2(String indicatorName2) {
        this.indicatorName2 = indicatorName2;
    }

    public String getIndicatorNumber() {
        return indicatorNumber;
    }

    public void setIndicatorNumber(String indicatorNumber) {
        this.indicatorNumber = indicatorNumber;
    }

    public String getIndicatorGrowthRate() {
        return indicatorGrowthRate;
    }

    public void setIndicatorGrowthRate(String indicatorGrowthRate) {
        this.indicatorGrowthRate = indicatorGrowthRate;
    }

    public List<String> getXAxis() {
        return xAxis;
    }

    public void setXAxis(List<String> xAxis) {
        this.xAxis = xAxis;
    }

    public List<String> getYAxis() {
        return yAxis;
    }

    public void setYAxis(List<String> yAxis) {
        this.yAxis = yAxis;
    }
}
