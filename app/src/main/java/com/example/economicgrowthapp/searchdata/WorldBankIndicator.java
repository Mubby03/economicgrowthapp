package com.example.economicgrowthapp.searchdata;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class WorldBankIndicator implements Parcelable {
    private String countryName;
    private String countryCode;
    private String seriesName;
    private String seriesCode;
    private Map<String, Double> yearData; // Or use another appropriate data structure for year data

    public WorldBankIndicator() {
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.seriesName = seriesName;
        this.seriesCode = seriesCode;
        this.yearData = yearData;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesCode() {
        return seriesCode;
    }

    public void setSeriesCode(String seriesCode) {
        this.seriesCode = seriesCode;
    }

    public Map<String, Double> getYearData() {
        return yearData;
    }

    public void setYearData(Map<String, Double> yearData) {
        this.yearData = yearData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(countryName);
        dest.writeString(countryCode);
        dest.writeString(seriesName);
        dest.writeString(seriesCode);
        dest.writeMap(yearData); // Assuming yearData is a Map<String, Double>
    }

    public static final Parcelable.Creator<WorldBankIndicator> CREATOR = new Parcelable.Creator<WorldBankIndicator>() {
        @Override
        public WorldBankIndicator createFromParcel(Parcel in) {
            return new WorldBankIndicator(in);
        }

        @Override
        public WorldBankIndicator[] newArray(int size) {
            return new WorldBankIndicator[size];
        }
    };

    protected WorldBankIndicator(Parcel in) {
        countryName = in.readString();
        countryCode = in.readString();
        seriesName = in.readString();
        seriesCode = in.readString();
        yearData = in.readHashMap(Double.class.getClassLoader()); // Assuming yearData is a Map<String, Double>
    }

// Constructors, getters, setters, etc.
}
