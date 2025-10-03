package com.example.gorail.adapters;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorail.R;
import com.example.gorail.model.Passenger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {

    private List<Passenger> passengerList;
    private Context context;

    public interface OnPassengerEditListener {
        void onEdit(Passenger passenger);
    }

    private OnPassengerEditListener editListener;

    public void setOnPassengerEditListener(OnPassengerEditListener listener) {
        this.editListener = listener;
    }


    public PassengerAdapter(Context context, List<Passenger> passengerList) {
        this.context = context;
        this.passengerList = passengerList;
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_passenger, parent, false);
        return new PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolder holder, int position) {
        Passenger passenger = passengerList.get(position);

        holder.tvName.setText(passenger.getName() + ", " + passenger.getAge() + " (" + passenger.getGender() + ")");
        holder.tvBerth.setText(passenger.getBerth());

        if (passenger.isSelected()) {
            holder.ivCheckmark.setImageResource(R.drawable.baseline_check_24);
            holder.ivCheckmark.setBackgroundResource(R.drawable.green_circle_background);
            holder.ivCheckmark.setColorFilter(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.ivCheckmark.setImageDrawable(null); // ❌ remove check icon
            holder.ivCheckmark.setBackgroundResource(R.drawable.grey_circle_background);
        }

        // ✅ Select/deselect on card click
        holder.itemView.setOnClickListener(v -> {
            passenger.setSelected(!passenger.isSelected());
            notifyItemChanged(position);
        });

        // ❌ Delete Logic remains same
        holder.ivDelete.setOnClickListener(v -> {
            // delete logic
        });


        holder.tvEdit.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEdit(passengerList.get(position));
            }
        });




        holder.ivDelete.setOnClickListener(v -> {
            holder.ivDelete.setEnabled(false); // prevent double click

            int currentPosition = holder.getAdapterPosition();

            if (currentPosition != RecyclerView.NO_POSITION) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String passengerId = passenger.getId();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                        .child(uid)
                        .child("PassengersList")
                        .child(passengerId);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ref.removeValue().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    int adapterPosition = holder.getAdapterPosition();
                                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < passengerList.size()) {
                                        passengerList.remove(adapterPosition);
                                        notifyItemRemoved(adapterPosition);
                                        Toast.makeText(context, "Passenger deleted", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                    holder.ivDelete.setEnabled(true); // enable again on failure
                                }
                            });
                        } else {
                            Toast.makeText(context, "Passenger already deleted", Toast.LENGTH_SHORT).show();
                            holder.ivDelete.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        holder.ivDelete.setEnabled(true);
                    }
                });
            } else {
                holder.ivDelete.setEnabled(true);
            }
        });



    }

    @Override
    public int getItemCount() {
        return passengerList.size();
    }

    public static class PassengerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBerth, tvEdit;
        ImageView ivDelete,ivCheckmark;

        public PassengerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPassengerName);
            tvBerth = itemView.findViewById(R.id.tvPassengerBerth);
            tvEdit = itemView.findViewById(R.id.tvEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivCheckmark = itemView.findViewById(R.id.ivCheckmark);
        }
    }
}
