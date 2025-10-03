package com.example.gorail.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorail.R;
import com.example.gorail.model.TrainStatusModel;

import java.util.List;

public class TrainStatusAdapter extends RecyclerView.Adapter<TrainStatusAdapter.StatusViewHolder> {

    private Context context;
    private List<TrainStatusModel> statusList;

    public TrainStatusAdapter(Context context, List<TrainStatusModel> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        TrainStatusModel item = statusList.get(position);

        holder.tvStationCode.setText(item.stationCode);
        holder.tvStationName.setText(item.stationName);
        holder.tvStationType.setText(item.stationType);
        holder.tvDistance.setText(item.distance);

        holder.tvScheduledArrival.setText(item.scheduledArrival);
        holder.tvActualArrival.setText(item.actualArrival);

        holder.tvScheduledDeparture.setText(item.scheduledDeparture);
        holder.tvActualDeparture.setText(item.actualDeparture);

        holder.tvPlatformNumber.setText(item.platformNumber);
        holder.tvStopTime.setText(item.stopTime);
        holder.tvDelayStatus.setText(item.delayStatus);

        // Handle first and last dot connection
        if (position == 0) {
            holder.viewLineTop.setVisibility(View.INVISIBLE);
        } else {
            holder.viewLineTop.setVisibility(View.VISIBLE);
        }

        if (position == statusList.size() - 1) {
            holder.viewLineBottom.setVisibility(View.INVISIBLE);
        } else {
            holder.viewLineBottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView tvStationCode, tvStationName, tvStationType, tvDistance;
        TextView tvScheduledArrival, tvActualArrival;
        TextView tvScheduledDeparture, tvActualDeparture;
        TextView tvPlatformNumber, tvStopTime, tvDelayStatus;
        View viewLineTop, viewLineBottom;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStationCode = itemView.findViewById(R.id.tvStationCode);
            tvStationName = itemView.findViewById(R.id.tvStationName);
            tvStationType = itemView.findViewById(R.id.tvStationType);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvScheduledArrival = itemView.findViewById(R.id.tvScheduledArrival);
            tvActualArrival = itemView.findViewById(R.id.tvActualArrival);
            tvScheduledDeparture = itemView.findViewById(R.id.tvScheduledDeparture);
            tvActualDeparture = itemView.findViewById(R.id.tvActualDeparture);
            tvPlatformNumber = itemView.findViewById(R.id.tvPlatformNumber);
            tvStopTime = itemView.findViewById(R.id.tvStopTime);
            tvDelayStatus = itemView.findViewById(R.id.tvDelayStatus);
            viewLineTop = itemView.findViewById(R.id.viewLineTop);
            viewLineBottom = itemView.findViewById(R.id.viewLineBottom);
        }
    }
}
