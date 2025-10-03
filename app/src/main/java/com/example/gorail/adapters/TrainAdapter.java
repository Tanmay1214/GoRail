package com.example.gorail.adapters;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorail.BookingActivity;
import com.example.gorail.FindTrainsActivity;
import com.example.gorail.R;
import com.example.gorail.model.TrainData;

import java.util.Calendar;
import java.util.List;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.TrainViewHolder> {
    private String adultPassengers;
    private String childPassengers;

    private String date;

    private boolean isInMaintenanceWindow() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        // Convert time to minutes past midnight
        int currentMinutes = hour * 60 + minute;

        // Maintenance from 11:40 PM (1420 mins) to 12:20 AM (20 mins next day)
        return (currentMinutes >= 1420 || currentMinutes < 20);
    }




    private List<TrainData> trainList;

    public TrainAdapter(List<TrainData> trainList, FindTrainsActivity findTrainsActivity, String adultPassengers, String childPassengers, String date) {
        this.trainList = trainList;
        this.adultPassengers = adultPassengers;
        this.childPassengers = childPassengers;
        this.date = date;
    }

    @NonNull
    @Override
    public TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_trains, parent, false);
        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainViewHolder holder, int position) {
        TrainData train = trainList.get(position);
        holder.trainName.setText(train.train_name);
        holder.trainNo.setText(train.train_no);
        holder.fromTime.setText(train.from_time);
        holder.toTime.setText(train.to_time);
        holder.travelTime.setText(train.travel_time + "hr");
        holder.fromStation.setText(train.from_stn_code);
        holder.toStation.setText(train.to_stn_code);

        setRunningDays(holder, train.getRunning_days());

        // Hide all ticket cards initially
        hideAllClassCards(holder);




        // ✅ Seat Availability Safe Check
        if (isInMaintenanceWindow()) {
            holder.ticketOptionsScrollView.setVisibility(View.GONE);
            holder.maintananceLayout.setVisibility(View.VISIBLE);
            holder.maintenanceMessage.setText("Railway services are down for maintenance from 11:40 PM to 12:20 AM. Please try again later.");

        } else if (train.apiErrorMessage != null && train.apiErrorMessage.equalsIgnoreCase("IRCTC Down")) {
            holder.ticketOptionsScrollView.setVisibility(View.GONE);
            holder.maintananceLayout.setVisibility(View.VISIBLE);
            holder.maintenanceMessage.setText("IRCTC services are currently unavailable. Please try again later.");

        } else if (train.isSeatLoading) {
            showShimmerCards(holder);
            holder.maintananceLayout.setVisibility(View.GONE);

        } else if (train.classAvailability != null) {
            holder.maintananceLayout.setVisibility(View.GONE);
            holder.ticketOptionsScrollView.setVisibility(View.VISIBLE);

            for (TrainData.TrainClassAvailability classData : train.classAvailability) {
                if (classData.className == null || classData.seatStatus == null || classData.totalFare == null) {
                    continue;
                }
                Log.d(TAG, "🔹 Mapping Class: " + classData.className);

                switch (classData.className) {
                    case "1A":
                        showClassCard(holder.classCard1A, holder.status1A, holder.price1AText, classData);
                        holder.classCard1A.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String price = holder.price1AText.getText().toString();
                                String status = holder.status1A.getText().toString().trim();

                                Log.d("TrainAdapter", "Clicked 1A - Price: " + price + ", Status: " + status);

                                if (status.equalsIgnoreCase("TRAIN DEPARTED") || status.equalsIgnoreCase("NOT AVAILABLE")) {
                                    Log.d("TrainAdapter", "Click ignored due to invalid status.");
                                    return;
                                }

                                Intent intent = new Intent(v.getContext(), BookingActivity.class);
                                intent.putExtra("classType", "1A");
                                intent.putExtra("trainNumber", train.train_no);
                                intent.putExtra("status", status);
                                intent.putExtra("date", date);
                                intent.putExtra("departureTime", train.from_time);
                                intent.putExtra("arrivalTime", train.to_time);
                                intent.putExtra("travelTime", train.getTravel_time());
                                intent.putExtra("fromStationName", train.from_stn_name);
                                intent.putExtra("toStationName", train.to_stn_name);
                                intent.putExtra("trainName", train.train_name);
                                intent.putExtra("toStation", train.to_stn_code);
                                intent.putExtra("fromStation", train.from_stn_code);
                                intent.putExtra("adultNumber", adultPassengers);
                                intent.putExtra("childNumber", childPassengers);
                                v.getContext().startActivity(intent);
                            }
                        });
                        break;

                    case "2A":
                        showClassCard(holder.classCard2A, holder.status2A, holder.price2AText, classData);
                        holder.classCard2A.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String price = holder.price2AText.getText().toString();
                                String status = holder.status2A.getText().toString();

                                Log.d("TrainAdapter", "Clicked 2A - Price: " + price + ", Status: " + status + "fromStationName:" + train.from_stn_name);

                                if (status.equalsIgnoreCase("TRAIN DEPARTED") || status.equalsIgnoreCase("NOT AVAILABLE")) {
                                    Log.d("TrainAdapter", "Click ignored due to invalid status.");
                                    return;
                                }

                                Intent intent = new Intent(v.getContext(), BookingActivity.class);
                                intent.putExtra("classType", "2A");
                                intent.putExtra("trainNumber", train.train_no);
                                intent.putExtra("status", status);
                                intent.putExtra("date", date);
                                intent.putExtra("departureTime", train.from_time);
                                intent.putExtra("arrivalTime", train.to_time);
                                intent.putExtra("travelTime", train.getTravel_time());
                                intent.putExtra("trainName", train.train_name);
                                intent.putExtra("fromStationName", train.from_stn_name);
                                intent.putExtra("toStationName", train.to_stn_name);
                                intent.putExtra("toStation", train.to_stn_code);
                                intent.putExtra("fromStation", train.from_stn_code);
                                intent.putExtra("adultNumber", adultPassengers);
                                intent.putExtra("childNumber", childPassengers);
                                v.getContext().startActivity(intent);
                            }
                        });
                        break;
                    case "3A":
                        showClassCard(holder.classCard3A, holder.status3A, holder.price3AText, classData);
                        holder.classCard3A.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String price = holder.price3AText.getText().toString();
                                String status = holder.status3A.getText().toString();

                                Log.d("TrainAdapter", "Clicked 3A - Price: " + price + ", Status: " + status);

                                if (status.equalsIgnoreCase("TRAIN DEPARTED") || status.equalsIgnoreCase("NOT AVAILABLE")) {
                                    Log.d("TrainAdapter", "Click ignored due to invalid status.");
                                    return;
                                }

                                Intent intent = new Intent(v.getContext(), BookingActivity.class);
                                intent.putExtra("classType", "3A");
                                intent.putExtra("trainNumber", train.train_no);
                                intent.putExtra("status", status);
                                intent.putExtra("date", date);
                                intent.putExtra("departureTime", train.from_time);
                                intent.putExtra("arrivalTime", train.to_time);
                                intent.putExtra("travelTime", train.getTravel_time());
                                intent.putExtra("trainName", train.train_name);
                                intent.putExtra("fromStationName", train.from_stn_name);
                                intent.putExtra("toStationName", train.to_stn_name);
                                intent.putExtra("toStation", train.to_stn_code);
                                intent.putExtra("fromStation", train.from_stn_code);
                                intent.putExtra("adultNumber", adultPassengers);
                                intent.putExtra("childNumber", childPassengers);
                                v.getContext().startActivity(intent);
                            }
                        });
                        break;
                    case "SL":
                        showClassCard(holder.classCardSL, holder.statusSL, holder.priceSLText, classData);
                        holder.classCardSL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String price = holder.priceSLText.getText().toString();
                                String status = holder.statusSL.getText().toString();

                                Log.d("TrainAdapter", "Clicked SL - Price: " + price + ", Status: " + status);

                                if (status.equalsIgnoreCase("TRAIN DEPARTED") || status.equalsIgnoreCase("NOT AVAILABLE")) {
                                    Log.d("TrainAdapter", "Click ignored due to invalid status.");
                                    return;
                                }

                                Intent intent = new Intent(v.getContext(), BookingActivity.class);
                                intent.putExtra("classType", "SL");
                                intent.putExtra("trainNumber", train.train_no);
                                intent.putExtra("status", status);
                                intent.putExtra("date", date);
                                intent.putExtra("departureTime", train.from_time);
                                intent.putExtra("arrivalTime", train.to_time);
                                intent.putExtra("travelTime", train.getTravel_time());
                                intent.putExtra("trainName", train.train_name);
                                intent.putExtra("fromStationName", train.from_stn_name);
                                intent.putExtra("toStationName", train.to_stn_name);
                                intent.putExtra("toStation", train.to_stn_code);
                                intent.putExtra("fromStation", train.from_stn_code);
                                intent.putExtra("adultNumber", adultPassengers);
                                intent.putExtra("childNumber", childPassengers);
                                v.getContext().startActivity(intent);
                            }
                        });
                        break;
                    case "3E":
                        showClassCard(holder.classCard3E, holder.status3E, holder.price3EText, classData);
                        holder.classCard3E.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String price = holder.price3EText.getText().toString();
                                String status = holder.status3E.getText().toString();

                                Log.d("TrainAdapter", "Clicked 3E - Price: " + price + ", Status: " + status);

                                if (status.equalsIgnoreCase("TRAIN DEPARTED") || status.equalsIgnoreCase("NOT AVAILABLE")) {
                                    Log.d("TrainAdapter", "Click ignored due to invalid status.");
                                    return;
                                }

                                Intent intent = new Intent(v.getContext(), BookingActivity.class);
                                intent.putExtra("classType", "3E");
                                intent.putExtra("trainNumber", train.train_no);
                                intent.putExtra("status", status);
                                intent.putExtra("date", date);
                                intent.putExtra("departureTime", train.from_time);
                                intent.putExtra("arrivalTime", train.to_time);
                                intent.putExtra("travelTime", train.getTravel_time());
                                intent.putExtra("trainName", train.train_name);
                                intent.putExtra("fromStationName", train.from_stn_name);
                                intent.putExtra("toStationName", train.to_stn_name);
                                intent.putExtra("toStation", train.to_stn_code);
                                intent.putExtra("fromStation", train.from_stn_code);
                                intent.putExtra("adultNumber", adultPassengers);
                                intent.putExtra("childNumber", childPassengers);
                                v.getContext().startActivity(intent);
                            }
                        });
                        break;
                    case "CC":
                        showClassCard(holder.classCardCC, holder.statusCC, holder.priceCCText, classData);
                        holder.classCardCC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String price = holder.priceCCText.getText().toString();
                                String status = holder.statusCC.getText().toString();

                                Log.d("TrainAdapter", "Clicked CC - Price: " + price + ", Status: " + status);

                                if (status.equalsIgnoreCase("TRAIN DEPARTED") || status.equalsIgnoreCase("NOT AVAILABLE")) {
                                    Log.d("TrainAdapter", "Click ignored due to invalid status.");
                                    return;
                                }

                                Intent intent = new Intent(v.getContext(), BookingActivity.class);
                                intent.putExtra("classType", "CC");
                                intent.putExtra("trainNumber", train.train_no);
                                intent.putExtra("status", status);
                                intent.putExtra("date", date);
                                intent.putExtra("departureTime", train.from_time);
                                intent.putExtra("arrivalTime", train.to_time);
                                intent.putExtra("travelTime", train.getTravel_time());
                                intent.putExtra("trainName", train.train_name);
                                intent.putExtra("fromStationName", train.from_stn_name);
                                intent.putExtra("toStationName", train.to_stn_name);
                                intent.putExtra("toStation", train.to_stn_code);
                                intent.putExtra("fromStation", train.from_stn_code);
                                intent.putExtra("adultNumber", adultPassengers);
                                intent.putExtra("childNumber", childPassengers);
                                v.getContext().startActivity(intent);
                            }
                        });
                        break;
                    case "EC":
                        showClassCard(holder.classCardEC, holder.statusEC, holder.priceECText, classData);
                        holder.classCardEC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String price = holder.priceECText.getText().toString();
                                String status = holder.statusEC.getText().toString();



                                Log.d("TrainAdapter", "Clicked 1A - Price: " + price + ", Status: " + status);

                                if (status.equalsIgnoreCase("TRAIN DEPARTED") || status.equalsIgnoreCase("NOT AVAILABLE")) {
                                    Log.d("TrainAdapter", "Click ignored due to invalid status.");
                                    return;
                                }

                                Intent intent = new Intent(v.getContext(), BookingActivity.class);
                                intent.putExtra("classType", "EC");
                                intent.putExtra("trainNumber", train.train_no);
                                intent.putExtra("status", status);
                                intent.putExtra("date", date);
                                intent.putExtra("departureTime", train.from_time);
                                intent.putExtra("arrivalTime", train.to_time);
                                intent.putExtra("travelTime", train.getTravel_time());
                                intent.putExtra("trainName", train.train_name);
                                intent.putExtra("fromStationName", train.from_stn_name);
                                intent.putExtra("toStationName", train.to_stn_name);
                                intent.putExtra("toStation", train.to_stn_code);
                                intent.putExtra("fromStation", train.from_stn_code);
                                intent.putExtra("adultNumber", adultPassengers);
                                intent.putExtra("childNumber", childPassengers);
                                v.getContext().startActivity(intent);
                            }
                        });
                        break;
                    case "2S":
                        showClassCard(holder.classCard2S, holder.status2S, holder.price2SText, classData);
                        holder.classCard2S.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String price = holder.price2SText.getText().toString();
                                String status = holder.status2A.getText().toString();

                                Log.d("TrainAdapter", "Clicked 2S - Price: " + price + ", Status: " + status);

                                if (status.equalsIgnoreCase("TRAIN DEPARTED") || status.equalsIgnoreCase("NOT AVAILABLE")) {
                                    Log.d("TrainAdapter", "Click ignored due to invalid status.");
                                    return;
                                }

                                Intent intent = new Intent(v.getContext(), BookingActivity.class);
                                intent.putExtra("classType", "2S");
                                intent.putExtra("trainNumber", train.train_no);
                                intent.putExtra("status", status);
                                intent.putExtra("date", date);
                                intent.putExtra("departureTime", train.from_time);
                                intent.putExtra("arrivalTime", train.to_time);
                                intent.putExtra("travelTime", train.getTravel_time());
                                intent.putExtra("trainName", train.train_name);
                                intent.putExtra("fromStationName", train.from_stn_name);
                                intent.putExtra("toStationName", train.to_stn_name);
                                intent.putExtra("toStation", train.to_stn_code);
                                intent.putExtra("fromStation", train.from_stn_code);
                                intent.putExtra("adultNumber", adultPassengers);
                                intent.putExtra("childNumber", childPassengers);
                                v.getContext().startActivity(intent);
                            }
                        });

                        break;
                }
            }
        }



    }

    @Override
    public int getItemCount() {
        return trainList.size();
    }

    private void setRunningDays(TrainViewHolder holder, String runningDays) {
        if (runningDays == null || runningDays.length() != 7) {
            return;
        }

        TextView[] dayViews = {
                holder.sundayTextView, holder.mondayTextView, holder.tuesdayTextView,
                holder.wednesdayTextView, holder.thursdayTextView, holder.fridayTextView,
                holder.saturdayTextView
        };

        for (int i = 0; i < 7; i++) {
            if (runningDays.charAt(i) == '1') {
                dayViews[i].setTextColor(Color.parseColor("#0C5C3F")); // Active Day
            } else {
                dayViews[i].setTextColor(Color.GRAY);  // Inactive Day
            }
        }
    }

    private void hideAllClassCards(TrainViewHolder holder) {
        holder.classCard1A.setVisibility(View.GONE);
        holder.classCard2A.setVisibility(View.GONE);
        holder.classCard3A.setVisibility(View.GONE);
        holder.classCardSL.setVisibility(View.GONE);
        holder.classCard3E.setVisibility(View.GONE);
        holder.classCardCC.setVisibility(View.GONE);
        holder.classCardEC.setVisibility(View.GONE);
        holder.classCard2S.setVisibility(View.GONE);
    }

    private void showClassCard(CardView classCard, TextView statusView, TextView priceView, TrainData.TrainClassAvailability classData) {
        classCard.setVisibility(View.VISIBLE);
        statusView.setText(classData.seatStatus);
        priceView.setText("₹" + classData.totalFare);

        // ✅ Set background & text color according to seatStatus
        String seatStatus = classData.seatStatus.toUpperCase();  // case-insensitive

        if (seatStatus.startsWith("AVAILABLE") || seatStatus.startsWith("CURR_AVBL")) {
            classCard.setCardBackgroundColor(ContextCompat.getColor(classCard.getContext(), R.color.curr_avbl_background));
            statusView.setTextColor(ContextCompat.getColor(statusView.getContext(), R.color.curr_avbl_text));
        } else if (seatStatus.startsWith("RAC")) {
            classCard.setCardBackgroundColor(ContextCompat.getColor(classCard.getContext(), R.color.rac_background));
            statusView.setTextColor(ContextCompat.getColor(statusView.getContext(), R.color.rac_text)); // RAC ke liye black readable hoga
        } else if (seatStatus.contains("WL") || seatStatus.contains("GNWL") || seatStatus.contains("RLWL") || seatStatus.contains("PQWL")) {
            classCard.setCardBackgroundColor(ContextCompat.getColor(classCard.getContext(), R.color.wl_background));
            statusView.setTextColor(ContextCompat.getColor(statusView.getContext(), R.color.wl_text));  // WL ke liye white text
        } else if (seatStatus.contains("REGRET") || seatStatus.contains("NOT AVAILABLE") || seatStatus.contains("TRAIN DEPARTED")) {
            classCard.setCardBackgroundColor(ContextCompat.getColor(classCard.getContext(), R.color.grayDark));
            statusView.setTextColor(ContextCompat.getColor(statusView.getContext(), R.color.white));  // Gray background ke liye white text
        } else {
            classCard.setCardBackgroundColor(ContextCompat.getColor(classCard.getContext(), R.color.defaultCard));  // fallback
            statusView.setTextColor(ContextCompat.getColor(statusView.getContext(), R.color.black));  // Default ke liye black
        }
    }





    private void showShimmerCards(TrainViewHolder holder) {
        holder.classCard1A.setVisibility(View.VISIBLE);
        holder.status1A.setText("");
        holder.price1AText.setText("Loading");

        holder.classCard2A.setVisibility(View.VISIBLE);
        holder.status2A.setText("");
        holder.price2AText.setText("Loading");

        holder.classCardSL.setVisibility(View.VISIBLE);
        holder.statusSL.setText("");
        holder.priceSLText.setText("Loading");

        // add more if needed (or keep these 3 as default shimmer look)
    }


    public static class TrainViewHolder extends RecyclerView.ViewHolder {

        LinearLayout expandableTable, maintananceLayout;

        HorizontalScrollView ticketOptionsScrollView;
        TextView trainName, trainNo, fromTime, toTime, travelTime, fromStation, toStation, maintenanceMessage;
        TextView status1A, status2A, status3A, statusSL, status3E, statusCC, statusEC, status2S;
        TextView price1AText, price2AText, price3AText, priceSLText, price3EText, priceCCText, priceECText, price2SText;
        CardView classCard1A, classCard2A, classCard3A, classCardSL, classCard3E, classCardCC, classCardEC, classCard2S;
        TextView sundayTextView, mondayTextView, tuesdayTextView, wednesdayTextView, thursdayTextView, fridayTextView, saturdayTextView;

        public TrainViewHolder(@NonNull View itemView) {
            super(itemView);
            trainName = itemView.findViewById(R.id.trainName);
            trainNo = itemView.findViewById(R.id.trainNo);
            fromTime = itemView.findViewById(R.id.fromTime);
            toTime = itemView.findViewById(R.id.toTime);
            travelTime = itemView.findViewById(R.id.travelTime);
            fromStation = itemView.findViewById(R.id.fromStation);
            toStation = itemView.findViewById(R.id.toStation);

            sundayTextView = itemView.findViewById(R.id.sundayTextView);
            mondayTextView = itemView.findViewById(R.id.mondayTextView);
            tuesdayTextView = itemView.findViewById(R.id.tuesdayTextView);
            wednesdayTextView = itemView.findViewById(R.id.wednesdayTextView);
            thursdayTextView = itemView.findViewById(R.id.thursdayTextView);
            fridayTextView = itemView.findViewById(R.id.fridayTextView);
            saturdayTextView = itemView.findViewById(R.id.saturdayTextView);

            // ✅ Seat Availability Fields
            status1A = itemView.findViewById(R.id.status1A);
            status2A = itemView.findViewById(R.id.status2A);
            status3A = itemView.findViewById(R.id.status3A);
            statusSL = itemView.findViewById(R.id.statusSL);
            status3E = itemView.findViewById(R.id.status3E);
            statusCC = itemView.findViewById(R.id.statusCC);
            statusEC = itemView.findViewById(R.id.statusEC);
            status2S = itemView.findViewById(R.id.status2S);

            price1AText = itemView.findViewById(R.id.price1AText);
            price2AText = itemView.findViewById(R.id.price2AText);
            price3AText = itemView.findViewById(R.id.price3AText);
            priceSLText = itemView.findViewById(R.id.priceSLText);
            price3EText = itemView.findViewById(R.id.price3EText);
            priceCCText = itemView.findViewById(R.id.priceCCText);
            priceECText = itemView.findViewById(R.id.priceECText);
            price2SText = itemView.findViewById(R.id.price2SText);

            classCard1A = itemView.findViewById(R.id.ticket1ACard);
            classCard2A = itemView.findViewById(R.id.ticket2ACard);
            classCard3A = itemView.findViewById(R.id.ticket3ACard);
            classCardSL = itemView.findViewById(R.id.ticketSLCard);
            classCard3E = itemView.findViewById(R.id.ticket3ECard);
            classCardCC = itemView.findViewById(R.id.ticketCCCard);
            classCardEC = itemView.findViewById(R.id.ticketECCard);
            classCard2S = itemView.findViewById(R.id.ticket2SCard);

            maintananceLayout = itemView.findViewById(R.id.maintenanceLayout);
            maintenanceMessage = itemView.findViewById(R.id.maintenanceMessage);

            ticketOptionsScrollView = itemView.findViewById(R.id.ticketOptionsScrollView);

        }
    }
}