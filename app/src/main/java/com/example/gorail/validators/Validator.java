package com.example.gorail.validators;

import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.example.gorail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Validator {
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");

    // Validate Name (Only alphabets and spaces, not empty)
    public static boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z\\s]+") && !name.trim().isEmpty();
    }

    // Validate Email (Proper email format)
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validate Phone Number (+91 followed by 10 digits)
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\+91[6-9]\\d{9}");
    }

    // Check if email already exists in Realtime Database (Registration)
    public static void checkEmailExists(String email, OnEmailCheckListener listener) {
        dbRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        listener.onCheck(snapshot.exists());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onFailure(error.toException());
                    }
                });
    }

    // Check if phone already exists in Realtime Database (Registration)
    public static void checkPhoneExists(String phone, OnPhoneCheckListener listener) {
        dbRef.orderByChild("phone").equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        listener.onCheck(snapshot.exists());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onFailure(error.toException());
                    }
                });
    }

    // For Login: Check if phone exists; if not, return error
    public static void checkPhoneForLogin(String phone, OnPhoneCheckListener listener) {
        dbRef.orderByChild("phone").equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        listener.onCheck(snapshot.exists());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onFailure(error.toException());
                    }
                });
    }

    // Combined validation method for Registration Screen
    public static String validateAllFields(String name, String email, String phone) {
        if (!isValidName(name)) {
            return "Invalid Name: Name must contain only alphabets and spaces.";
        }
        if (!isValidEmail(email)) {
            return "Invalid Email: Please enter a valid email address.";
        }
        if (!isValidPhone(phone)) {
            return "Invalid Phone: Phone number must start with +91 and have 10 digits.";
        }
        return null; // All fields are valid
    }

    // Interfaces for callback
    public interface OnEmailCheckListener {
        void onCheck(boolean exists);
        void onFailure(Exception e);
    }

    public interface OnPhoneCheckListener {
        void onCheck(boolean exists);
        void onFailure(Exception e);
    }

    private void showError(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        vibrateView(editText);
    }

    private void vibrateView(View view) {
        Animation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(5));
        view.startAnimation(shake);
    }
}
