package com.example.gorail.validators;

import android.content.Context;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ValidatorTrain {
    public static boolean validateInputs(Context context, TextView fromText, TextView toText, Spinner classSpinner, Spinner quotaSpinner) {
        if (fromText.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "Please select a source station", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (toText.getText().toString().trim().isEmpty()) {
            Toast.makeText(context, "Please select a destination station", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fromText.getText().toString().equals(toText.getText().toString())) {
            Toast.makeText(context, "Source and destination cannot be the same", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (classSpinner.getSelectedItem() == null) {
            Toast.makeText(context, "Please select a travel class", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (quotaSpinner.getSelectedItem() == null) {
            Toast.makeText(context, "Please select a quota", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
