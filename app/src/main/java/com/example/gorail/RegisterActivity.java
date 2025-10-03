package com.example.gorail;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gorail.validators.Validator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    ImageView backBtn;
    Button signUpButton, googleSignUpButton;
    TextView btnAlreadyAccount;
    EditText eTPhone, eTName, eTEmail;

    ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private GoogleSignInClient googleSignInClient;
    private String verificationId;
    private AlertDialog otpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signUpButton = findViewById(R.id.signupbtn);
        googleSignUpButton = findViewById(R.id.google_sign_in_button);
        btnAlreadyAccount = findViewById(R.id.btn_already_account);
        eTPhone = findViewById(R.id.phone_number_edittxt);
        eTName = findViewById(R.id.fullname_editxt);
        eTEmail = findViewById(R.id.email_edittxt);
        progressBar = findViewById(R.id.signupbar);
        backBtn = findViewById(R.id.back_btn1);

        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this,StartUp.class));

        });


        signUpButton.setOnClickListener(view -> {
            signUpButton.setEnabled(false);  // Disable button
            signUpButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY)); // Grey out
            progressBar.setVisibility(View.VISIBLE);  // Show progress bar
            validateInputs();
        });
        googleSignUpButton.setOnClickListener(v -> signInWithGoogle());

        btnAlreadyAccount.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        eTPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && (eTPhone.getText().toString().isEmpty())) {
                eTPhone.setText("+91");
                eTPhone.setSelection(eTPhone.getText().length());
            }
        });

        eTPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (!text.startsWith("+91")) {
                    eTPhone.setText("+91");
                    eTPhone.setSelection(eTPhone.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    checkUserExistsAndProceed(user);
                }
            } else {
                Toast.makeText(this, "Google Sign-In authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserExistsAndProceed(FirebaseUser user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        String userId = user.getUid();
        String name = user.getDisplayName() != null ? user.getDisplayName() : "User";

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // User doesn't exist, store details
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("name", user.getDisplayName());
                    userData.put("email", user.getEmail());
                    userData.put("phone", user.getPhoneNumber() != null ? user.getPhoneNumber() : "");

                    usersRef.child(userId).setValue(userData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Welcome " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to save user data.", Toast.LENGTH_SHORT).show();
                        }
                        navigateToMain();
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Hi " + name + ", Account Already Exists. Please Login.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void validateInputs() {
        String name = eTName.getText().toString().trim();
        String email = eTEmail.getText().toString().trim();
        String phone = eTPhone.getText().toString().trim();

        String validationError = Validator.validateAllFields(name, email, phone);
        if (validationError != null) {
            // Check which field has the error based on the error message
           if (validationError.contains("Name")) {
               showError(eTName, validationError);
               resetSignUpButton();
           } else if (validationError.contains("Email")) {
               resetSignUpButton();
               showError(eTEmail, validationError);
           } else if (validationError.contains("Phone")) {
               showError(eTPhone, validationError);
               resetSignUpButton();
           }
        }

            Validator.checkEmailExists(email, new Validator.OnEmailCheckListener() {
                @Override
                public void onCheck(boolean exists) {
                    if (exists) {
                        showError(eTEmail, "Email already exists.");
                        resetSignUpButton();
                    } else {
                        Validator.checkPhoneExists(phone, new Validator.OnPhoneCheckListener() {
                            @Override
                            public void onCheck(boolean phoneExists) {
                                if (phoneExists) {
                                    showError(eTPhone, "Phone number already exists.");
                                    resetSignUpButton();
                                } else {
                                    sendOtpToPhone(phone);
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getApplicationContext(), "Error checking phone: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                resetSignUpButton();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getApplicationContext(), "Error checking email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    resetSignUpButton();
                }
            });
        }

        private void sendOtpToPhone (String phoneNumber){
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                            signInWithPhoneAuthCredential(credential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {
                            Toast.makeText(RegisterActivity.this, "OTP sending failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            resetSignUpButton();
                        }

                        @Override
                        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                            RegisterActivity.this.verificationId = verificationId;
                            showOtpDialog();
                        }
                    })
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }

    private void showOtpDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View otpDialogView = inflater.inflate(R.layout.dialog_otp, null);
        Button verifyButton = otpDialogView.findViewById(R.id.verify_otp_btn);
        EditText[] otpBoxes = {
                otpDialogView.findViewById(R.id.otp_box_1),
                otpDialogView.findViewById(R.id.otp_box_2),
                otpDialogView.findViewById(R.id.otp_box_3),
                otpDialogView.findViewById(R.id.otp_box_4),
                otpDialogView.findViewById(R.id.otp_box_5),
                otpDialogView.findViewById(R.id.otp_box_6),
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setView(otpDialogView);
        AlertDialog otpDialog = builder.create();
        otpDialog.setCancelable(false);
        otpDialog.show();
        otpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        otpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        for (int i = 0; i < otpBoxes.length; i++) {
            final int index = i;

            otpBoxes[i].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) { // Handle backspace
                        if (otpBoxes[index].getText().toString().isEmpty() && index > 0) {
                            otpBoxes[index - 1].requestFocus();
                            otpBoxes[index - 1].setSelection(otpBoxes[index - 1].getText().length());
                        }
                    }
                }
                return false;
            });

            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty() && index < otpBoxes.length - 1) {
                        otpBoxes[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (index == otpBoxes.length - 1 && s.length() == 1) {
                        otpBoxes[index].clearFocus();  // Remove focus if last digit is entered
                    }
                }
            });
        }

        verifyButton.setOnClickListener(v -> {
            StringBuilder otpCode = new StringBuilder();
            for (EditText box : otpBoxes) {
                if (box.getText().toString().trim().isEmpty()) {
                    showError(box, "Please fill all OTP fields");
                    return;
                }
                otpCode.append(box.getText().toString().trim());
            }
            otpDialog.dismiss();
            verifyOtp(otpCode.toString());
        });
    }


    private void verifyOtp (String otp){
            if (verificationId != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithPhoneAuthCredential(credential);
            }
        }

        private void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(GONE);
                    saveUserToDatabase();
                } else {
                    Toast.makeText(this, "Incorrect OTP Entered.", Toast.LENGTH_SHORT).show();
                    resetSignUpButton();
                }
            });
        }

        private void saveUserToDatabase () {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                String userId = user.getUid();
                String email = eTEmail.getText().toString().trim();
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("name", eTName.getText().toString().trim());
                userData.put("email", email);
                userData.put("phone", eTPhone.getText().toString().trim());
                userData.put("emailVerified", false);

                usersRef.child(userId).setValue(userData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        linkEmailToUser(user, email);
                       sendVerificationEmail(user);
                        navigateToMain();
                    } else {
                        Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        private void navigateToMain () {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }

        private void showError (EditText editText, String errorMessage){
            editText.setError(errorMessage);
            vibrateView(editText);
        }

        private void vibrateView (View view){
            Animation shake = new TranslateAnimation(0, 10, 0, 0);
            shake.setDuration(500);
            shake.setInterpolator(new CycleInterpolator(5));
            view.startAnimation(shake);
        }

    private void resetSignUpButton() {
        progressBar.setVisibility(View.GONE);
        signUpButton.setEnabled(true);
        signUpButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimary));
    }
    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("RegisterActivity", "Verification email sent successfully.");
                        Toast.makeText(getApplicationContext(), "Welcome" +user.getDisplayName() + "! Verification email sent please Verify Email.", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("RegisterActivity", "Failed to send verification email: " + task.getException());
                        Toast.makeText(getApplicationContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void linkEmailToUser(FirebaseUser user, String email) {
        if (email.isEmpty()) {
            Log.e("RegisterActivity", "Email is empty, cannot link.");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, "TEMP_PASSWORD"); // Use a temp password
        user.linkWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("RegisterActivity", "Email linked successfully.");
                sendVerificationEmail(user);
            } else {
                Log.e("RegisterActivity", "Failed to link email: " + task.getException());
            }
        });
    }




}
