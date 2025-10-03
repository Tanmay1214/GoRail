package com.example.gorail.model;

import com.google.gson.annotations.SerializedName;

public class SeatDetails {
    @SerializedName("Total Seats")
    private int totalSeats;

    @SerializedName("Available")
    private int available;

    @SerializedName("RAC")
    private int rac;

    @SerializedName("GN WL")
    private int gnWaitlist;

    @SerializedName("TQ WL")
    private int tqWaitlist;

    // ✅ Getters
    public int getTotalSeats() { return totalSeats; }
    public int getAvailable() { return available; }
    public int getRac() { return rac; }
    public int getGnWaitlist() { return gnWaitlist; }
    public int getTqWaitlist() { return tqWaitlist; }
}
