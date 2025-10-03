package com.example.gorail.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorail.R;
import com.example.gorail.model.Station;

import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private final List<Station> stationList;
    private final OnStationClickListener listener;

    public interface OnStationClickListener {
        void onStationClick(Station station);
    }

    public StationAdapter(List<Station> stationList, OnStationClickListener listener) {
        this.stationList = stationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        Station station = stationList.get(position);

        // Set the station code in the yellow block
        holder.stationCode.setText(station.getStationCode());

        // Set the station name next to the code
        holder.stationName.setText(station.getStationName());

        // Handle click events
        holder.itemView.setOnClickListener(v -> listener.onStationClick(station));
    }

    @Override
    public int getItemCount() {
        return stationList.size();
    }

    public static class StationViewHolder extends RecyclerView.ViewHolder {
        TextView stationCode, stationName;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            stationCode = itemView.findViewById(R.id.stationcode); // Station Code (Yellow)
            stationName = itemView.findViewById(R.id.nearbyStation); // Station Name (Black)
        }
    }

    public void updateList(List<Station> newList) {
        stationList.clear();
        stationList.addAll(newList);
        notifyDataSetChanged();
    }
}
