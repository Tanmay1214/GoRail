package com.example.gorail.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorail.R;
import com.example.gorail.model.DateUtils;
import com.example.gorail.model.TrainData;

import java.util.List;
import java.util.Map;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private List<DateUtils> dateList;
    private List<TrainData> trainList;
    private String date;  // ✅ Track selected date
    private Context context;
    private OnDateClickListener onDateClickListener;
    private Map<String, String> seatStatusMap;

    public DateAdapter(List<DateUtils> dateList, String date, Context context, OnDateClickListener onDateClickListener) {
        this.dateList = dateList;
        this.date = date;
        this.context = context;
        this.onDateClickListener = onDateClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateUtils dateUtils = dateList.get(position);
        holder.tvDate.setText(dateUtils.getDay());

        String currentDate = dateUtils.getFullDate();
        String status = "..."; // Default state

        if (seatStatusMap != null && seatStatusMap.containsKey(currentDate)) {
            status = seatStatusMap.get(currentDate);
        }

        status = status.replaceAll("(?<!^)([A-Z])", " $1").trim(); // Add spaces correctly
        holder.tvAvailability.setText(status);

        // 🎨 **Set Indicator Color Based on Availability**
        int colorRes;
        if (status.equalsIgnoreCase("Available")) {
            colorRes = R.color.available_green;
        } else if (status.equalsIgnoreCase("Filling  Fast")) {
            colorRes = R.color.filling_yellow;
        } else if (status.equalsIgnoreCase("Few  Seats") || status.equalsIgnoreCase("No Seats")) {
            colorRes = R.color.few_red;
        } else {
            colorRes = android.R.color.darker_gray; // Default Grey
        }

        // ✅ Now correctly apply the color
        int finalColor = ContextCompat.getColor(context, colorRes);
        holder.viewIndicator.setBackgroundColor(finalColor);

        Log.d("DATE_ADAPTER", "Date: " + currentDate + ", Status: " + status);
        Log.d("DATE_ADAPTER", "Setting color: " + finalColor);

        // ✅ Highlight selected date
        if (currentDate.equals(date)) {
            holder.itemView.setBackgroundResource(R.drawable.date_item_bg_selected);
            holder.tvDate.setTextColor(Color.WHITE);
            holder.tvAvailability.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.date_item_bg);
            holder.tvDate.setTextColor(Color.BLACK);
            holder.tvAvailability.setTextColor(Color.GRAY);
        }

        // ✅ Click Event for selecting date
        holder.itemView.setOnClickListener(v -> {
            date = currentDate;
            notifyDataSetChanged();
            onDateClickListener.onDateSelected(date);
        });
    }



    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAvailability;
        View viewIndicator; // ✅ Added View for Indicator

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            viewIndicator = itemView.findViewById(R.id.viewIndicator); // ✅ Find Indicator View
        }
    }

    // ✅ Interface for handling date selection
    public interface OnDateClickListener {
        void onDateSelected(String date);
    }

    public void setSeatStatusMap(Map<String, String> seatStatusMap) {
        this.seatStatusMap = seatStatusMap;
        notifyDataSetChanged(); // Refresh Adapter after setting new data
    }
}
