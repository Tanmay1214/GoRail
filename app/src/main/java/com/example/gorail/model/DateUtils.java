package com.example.gorail.model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateUtils {
    private String day;
    private String fullDate;  // Full date for API requests
    private String availability;
    private int textColor;
    private int indicatorColor;

    public DateUtils(String day, String fullDate, String availability, int textColor, int indicatorColor) {
        this.day = day;
        this.fullDate = fullDate;
        this.availability = availability;
        this.textColor = textColor;
        this.indicatorColor = indicatorColor;
    }

    public String getDay() { return day; }
    public String getFullDate() { return fullDate; }  // Used for API calls
    public String getAvailability() { return availability; }
    public int getTextColor() { return textColor; }
    public int getIndicatorColor() { return indicatorColor; }

    // ✅ Get today's date in "dd/MM/yyyy" format
    public static String getTodayDate() {
        SimpleDateFormat apiFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return apiFormat.format(Calendar.getInstance().getTime());
    }

    // ✅ Generate upcoming 6 dates (selected date first, then next 5 days)
    public static List<DateUtils> generateUpcomingDates(String selectedDate) {
        List<DateUtils> dateList = new ArrayList<>();
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        SimpleDateFormat apiFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());  // ✅ FIXED FORMAT

        Calendar calendar = Calendar.getInstance();

        try {
            if (selectedDate != null && !selectedDate.isEmpty()) {
                calendar.setTime(apiFormat.parse(selectedDate));  // ✅ Correctly parse selected date
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DateUtils", "❌ Error parsing selectedDate: " + selectedDate);
        }

        // ✅ Add Selected Date First
        dateList.add(new DateUtils(
                displayFormat.format(calendar.getTime()), // "19 Mar"
                apiFormat.format(calendar.getTime()),     // "19-03-2025"
                "",
                0xFF000000,  // Black text
                0xFF008000   // Green indicator for selected
        ));

        // ✅ Add Next 5 Days
        for (int i = 1; i <= 5; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(new DateUtils(
                    displayFormat.format(calendar.getTime()),
                    apiFormat.format(calendar.getTime()),
                    "",
                    0xFF666666,  // Dark Gray text
                    0xFFCCCCCC   // Light Gray indicator
            ));
        }

        // ✅ Logging the final date list
        Log.d("DateUtils", "📅 Final Generated Date List (After Fix):");
        for (DateUtils d : dateList) {
            Log.d("DateUtils", "   📆 " + d.getFullDate() + " (" + d.getDay() + ")");
        }

        return dateList;
    }




}
