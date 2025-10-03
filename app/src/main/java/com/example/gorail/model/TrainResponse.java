package com.example.gorail.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TrainResponse {

    // ✅ Response Fields for fetchTrains() API

    @SerializedName("ErrorMsg")
    private ErrorMsg errorMsg;

    public ErrorMsg getErrorMsg() {
        return errorMsg;
    }

    public static class ErrorMsg {
        @SerializedName("ErrorMessage")
        private String errorMessage;

        public String getErrorMessage() {
            return errorMessage;
        }
    }
    @SerializedName("success")
    private boolean success;

    @SerializedName("time_stamp")
    private long timeStamp;

    @SerializedName("data") // ✅ Used in fetchTrains()
    public List<TrainWrapper> data;

    // ✅ Response Fields for Seat Availability API (fetchSeatAvailability())
    @SerializedName("trainNo")
    private String trainNo;

    @SerializedName("trainName")
    private String trainName;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("totalFare")
    private String totalFare;

    @SerializedName("EnqClassType")
    private EnqClassType enqClassType;

    @SerializedName("avlDayList")
    private List<AvlDay> avlDayList;

    // ✅ Getters (for fetchTrains())
    public boolean isSuccess() {
        return success;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public List<TrainWrapper> getTrainData() { // ✅ Used in fetchTrains()
        return data;
    }

    // ✅ Getters (for fetchSeatAvailability())
    public String getTrainNo() {
        return trainNo;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public EnqClassType getEnqClassType() {
        return enqClassType;
    }

    public List<AvlDay> getAvlDayList() {
        return avlDayList;
    }

    // ✅ Inner Class for Class Type (Example: "2A" -> "AC 2 Tier")
    public static class EnqClassType {
        @SerializedName("Code")
        private String code;

        @SerializedName("Text")
        private String text;

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }

    // ✅ Inner Class for Availability Data (fetchSeatAvailability())
    public static class AvlDay {
        @SerializedName("availablityDate")
        private String availablityDate;

        @SerializedName("availablityStatus")
        private String availablityStatus;

        public String getAvailablityDate() {
            return availablityDate;
        }

        public String getAvailablityStatus() {
            return availablityStatus;
        }
    }
}
