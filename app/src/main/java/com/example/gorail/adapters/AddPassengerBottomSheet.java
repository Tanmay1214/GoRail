package com.example.gorail.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.gorail.R;
import com.example.gorail.model.Passenger;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

public class AddPassengerBottomSheet extends BottomSheetDialogFragment {

    private OnPassengerAddedListener listener;

    public interface OnPassengerAddedListener {
        void onPassengerAdded(String passengerId,String name, int age, String gender, String berth);


    }



    public void setOnPassengerAddedListener(OnPassengerAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_add_passenger, container, false);

        EditText etName = view.findViewById(R.id.editTextName);
        EditText etAge = view.findViewById(R.id.editTextAge);
        RadioGroup rgGender = view.findViewById(R.id.radioGroupGender);
        RadioGroup rgBerth = view.findViewById(R.id.berthRadioGroup);
        Button btnSave = view.findViewById(R.id.buttonSave);
        ImageView ivClose = view.findViewById(R.id.ivClose);

        ivClose.setOnClickListener(v -> {
            // Delay to allow exit animation to play smoothly (optional)
            new Handler(Looper.getMainLooper()).postDelayed(this::dismiss, 50);
        });

        if (getArguments() != null && "edit".equals(getArguments().getString("mode"))) {
            // Prefill fields
            etName.setText(getArguments().getString("name"));
            etAge.setText(String.valueOf(getArguments().getInt("age")));

            String gender = getArguments().getString("gender");
            if (gender.equalsIgnoreCase("Male")) rgGender.check(R.id.radioMale);
            else if (gender.equalsIgnoreCase("Female")) rgGender.check(R.id.radioFemale);
            else rgGender.check(R.id.radioOthers);

            String berth = getArguments().getString("berth");
            switch (berth) {
                case "Lower": rgBerth.check(R.id.radioLower); break;
                case "Middle": rgBerth.check(R.id.radioMiddle); break;
                case "Upper": rgBerth.check(R.id.radioUpper); break;
                case "Side Lower": rgBerth.check(R.id.radioSideLower); break;
                case "Side Upper": rgBerth.check(R.id.radioSideUpper); break;
                default: rgBerth.check(R.id.radioNoBerth);
            }

            btnSave.setText("UPDATE");
        }



        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String ageText = etAge.getText().toString().trim();

            // Name validation: must be at least 3 characters
            if (name.isEmpty() || name.length() < 3) {
                Toast.makeText(getContext(), "Please enter a valid full name (min 3 characters)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!name.matches("^[a-zA-Z ]+$")) {
                Toast.makeText(getContext(), "Name should only contain letters and spaces", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ageText.isEmpty()) {
                Toast.makeText(getContext(), "Please enter age", Toast.LENGTH_SHORT).show();
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
                if (age < 04 || age > 99) {
                    Toast.makeText(getContext(), "Age must be valid", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid age entered", Toast.LENGTH_SHORT).show();
                return;
            }

            int genderId = rgGender.getCheckedRadioButtonId();
            int berthId = rgBerth.getCheckedRadioButtonId();

            if (genderId == -1 || berthId == -1) {
                Toast.makeText(getContext(), "Please select gender and berth", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedGender = view.findViewById(genderId);
            RadioButton selectedBerth = view.findViewById(berthId);

            if (selectedGender == null || selectedBerth == null) {
                Toast.makeText(getContext(), "Error reading selection", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = selectedGender.getText().toString();
            String berth = selectedBerth.getText().toString();

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                        .child(currentUser.getUid())
                        .child("PassengersList");

                Bundle args = getArguments();
                boolean isEdit = args != null && "edit".equals(args.getString("mode"));
                String passengerId = isEdit ? args.getString("id") : ref.push().getKey();

                Passenger passenger = new Passenger(passengerId, name, age, gender, berth);
                ref.child(passengerId).setValue(passenger);

                if (listener != null) {
                    listener.onPassengerAdded(passengerId, name, age, gender, berth);
                }

                Toast.makeText(getContext(), isEdit ? "Passenger updated" : "Passenger added", Toast.LENGTH_SHORT).show();
            }

            dismiss();
        });




        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context contextWithStyle = new ContextThemeWrapper(getContext(), R.style.BottomSheetDialogSlideAnim);
        BottomSheetDialog dialog = new BottomSheetDialog(contextWithStyle);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            View bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            // ✅ Disable enter/exit animation
            if (dialog.getWindow() != null) {
                dialog.getWindow().setWindowAnimations(0);
            }

            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED); // Optional: Direct expand

                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        });

        return dialog;
    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}
