package com.example.gorail.model;

import com.google.gson.annotations.SerializedName;

public class Station {
    @SerializedName("NameEn")
    private String stationName;

    @SerializedName("StationCode")
    private String stationCode;

    // ✅ Fix: Properly assign values in the constructor
    public Station(String stationName, String stationCode) {
        this.stationName = stationName;
        this.stationCode = stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStationCode() {
        return stationCode;
    }
}
