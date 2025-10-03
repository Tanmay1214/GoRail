package com.example.gorail.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrainRouteResponse {

    @SerializedName("filtered_route")
    private List<TrainRouteModel> filteredRoute;

    @SerializedName("full_route")
    private List<TrainRouteModel> fullRoute;

    public List<TrainRouteModel> getFilteredRoute() {
        return filteredRoute;
    }

    public List<TrainRouteModel> getFullRoute() {
        return fullRoute;
    }
}
