package com.example.gorail.model;

public class TrainStatusModel {
    public String stationCode, stationName, stationType;
    public String scheduledArrival, actualArrival;
    public String scheduledDeparture, actualDeparture;
    public String distance, delayStatus, platformNumber, stopTime;

    public TrainStatusModel(String stationCode, String stationName, String stationType,
                            String scheduledArrival, String actualArrival,
                            String scheduledDeparture, String actualDeparture,
                            String distance, String delayStatus, String platformNumber,
                            String stopTime) {
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.stationType = stationType;
        this.scheduledArrival = scheduledArrival;
        this.actualArrival = actualArrival;
        this.scheduledDeparture = scheduledDeparture;
        this.actualDeparture = actualDeparture;
        this.distance = distance;
        this.delayStatus = delayStatus;
        this.platformNumber = platformNumber;
        this.stopTime = stopTime;
    }
}

