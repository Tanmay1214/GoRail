package com.example.gorail.model;

public class TrainRouteModel {
    private String station_no;
    public String station_name;

    private String halt_time;
    private String arrival;
    private String departure;
    private String distance;
    private String platform;

    private int day;


    private String getHalt_time(){
        return halt_time;
    }
    public String getStation_no() {
        return station_no;
    }

    public String getStation_name() {
        return station_name;
    }

    public String getArrival() {
        return arrival;
    }

    public String getDeparture() {
        return departure;
    }


    public void setDay(int day) {
        this.day = day;
    }


    public int getDay() {
        return day;
    }


    public String getDistance() {
        return distance;
    }

    public String getPlatform() {
        return platform;
    }
}
