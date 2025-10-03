package com.example.gorail.model;

public class TrainStatusItem {
    private String station;
    private String station_code;
    private String actual_arrival;
    private String actual_departure;
    private String scheduled_arrival;
    private String scheduled_departure;
    private String delay;
    private String day;
    private String date;
    private String status;
    private String platform;
    private String intermediate;
    private String halt;
    private String distance;

    // Getters and Setters

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getStation_code() {
        return station_code;
    }

    public void setStation_code(String station_code) {
        this.station_code = station_code;
    }

    public String getActual_arrival() {
        return actual_arrival;
    }

    public void setActual_arrival(String actual_arrival) {
        this.actual_arrival = actual_arrival;
    }

    public String getActual_departure() {
        return actual_departure;
    }

    public void setActual_departure(String actual_departure) {
        this.actual_departure = actual_departure;
    }

    public String getScheduled_arrival() {
        return scheduled_arrival;
    }

    public void setScheduled_arrival(String scheduled_arrival) {
        this.scheduled_arrival = scheduled_arrival;
    }

    public String getScheduled_departure() {
        return scheduled_departure;
    }

    public void setScheduled_departure(String scheduled_departure) {
        this.scheduled_departure = scheduled_departure;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getIntermediate() {
        return intermediate;
    }

    public void setIntermediate(String intermediate) {
        this.intermediate = intermediate;
    }

    public String getHalt() {
        return halt;
    }

    public void setHalt(String halt) {
        this.halt = halt;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
