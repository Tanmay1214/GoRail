package com.example.gorail.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StationResponse {
    @SerializedName("ResponseCode")
    private String responseCode;

    @SerializedName("Status")
    private String status;

    @SerializedName("Station")
    private List<Station> stations;

    public String getResponseCode() {
        return responseCode;
    }

    public String getStatus() {
        return status;
    }

    public List<Station> getStations() {
        return stations;
    }
}
