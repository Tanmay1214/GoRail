package com.example.gorail.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorail.R;
import com.example.gorail.model.Station;

import java.util.List;

public class RecentSearchesAdapter extends RecyclerView.Adapter<RecentSearchesAdapter.ViewHolder> {
    private List<Station> recentSearches;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Station station);
    }

    public RecentSearchesAdapter(List<Station> recentSearches, OnItemClickListener listener) {
        this.recentSearches = recentSearches;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Station station = recentSearches.get(position);

        // 🔍 Debugging: Log each station before displaying
        Log.d("RecentSearchesAdapter", "Binding Station: " + station.getStationName() + " (" + station.getStationCode() + ")");

        if (station.getStationName() != null && station.getStationCode() != null) {
            holder.stationName.setText(station.getStationName() + " (" + station.getStationCode() + ")");
            holder.stationCode.setText(station.getStationCode());
        } else {
            holder.stationName.setText("Unknown Station"); // Fallback if data is null
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(station);
            }
        });
    }


    @Override
    public int getItemCount() {
        return recentSearches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView stationName,stationCode;

        ViewHolder(View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.nearbyStation);
            stationCode = itemView.findViewById(R.id.stationcode);
        }
    }
}
