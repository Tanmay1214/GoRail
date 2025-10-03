package com.example.gorail.model;

import java.util.ArrayList;
import java.util.List;

public class TrainData {
    public String train_no;
    public String train_name;
    public String from_stn_name;
    public String from_stn_code;
    public String to_stn_name;
    public String to_stn_code;
    public String from_time;
    public String to_time;
    public String travel_time;

    String running_days;
    String type;
    String train_id;
    String distance_from_to;
    String average_speed;
    String classSelected;

    public List<TrainClassAvailability> classAvailability;  // ✅ Seat availability list

    // ✅ Getters
    public String getTrain_no() { return train_no; }
    public String getTrain_name() { return train_name; }
    public String getFrom_stn_name() { return from_stn_name; }
    public String getFrom_stn_code() { return from_stn_code; }
    public String getTo_stn_name() { return to_stn_name; }
    public String getTo_stn_code() { return to_stn_code; }
    public String getFrom_time() { return from_time; }
    public String getTo_time() { return to_time; }
    public String getTravel_time() { return travel_time; }
    public String getRunning_days() { return running_days; }

    public String apiErrorMessage;
    public String getClassSelected() { return classSelected; }
    public String getType() { return type; }
    public String getTrain_id() { return train_id; }
    public String getDistance_from_to() { return distance_from_to; }
    public String getAverage_speed() { return average_speed; }

    // ✅ Seat Availability Getter (Null Check Included)
    public List<TrainClassAvailability> getClassAvailability() {
        if (classAvailability == null) {
            return new ArrayList<>(); // ✅ Return empty list if null
        }
        return classAvailability;
    }

    public boolean isSeatLoading = true;


    // ✅ Seat Availability Setter (Preserve Previous Data)
    public void setClassAvailability(List<TrainClassAvailability> newAvailability) {
        if (this.classAvailability == null) {
            this.classAvailability = new ArrayList<>();
        }
        this.classAvailability.addAll(newAvailability); // ✅ Add new data instead of overwriting
    }

    // ✅ Inner class for class-wise availability
    public static class TrainClassAvailability {
        public String className;  // Example: "3A", "2A", "SL"
        public String seatStatus; // Example: "AVAILABLE-10", "GNWL45/WL30"
        public String totalFare;

        public TrainClassAvailability(String className, String seatStatus, String totalFare) {
            this.className = className;
            this.seatStatus = seatStatus;
            this.totalFare = totalFare;
        }

        public String getClassName() { return className; }
        public String getSeatStatus() { return seatStatus; }
        public String getTotalFare() { return totalFare; }
    }
}
