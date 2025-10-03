package com.example.gorail;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
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
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText eTPhone;
    ImageView backBtn;
    TextView dontAccount;

    ProgressBar loginBar;
    Button loginBtn, googleLoginBtn;
    private static final int RC_SIGN_IN = 101;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    DatabaseReference usersRef;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks otpCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginBtn = findViewById(R.id.login_button);
        eTPhone = findViewById(R.id.phone_number_edittxt);
        dontAccount = findViewById(R.id.btn_donthave_account);
        backBtn = findViewById(R.id.back_btn2);
        loginBar = findViewById(R.id.progressBarLogin);
        googleLoginBtn = findViewById(R.id.google_sign_in_button);

        backBtn.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, StartUp.class)));

        eTPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && eTPhone.getText().toString().isEmpty()) {
                eTPhone.setText("+91");
                eTPhone.setSelection(eTPhone.getText().length());
            }
        });

        eTPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().startsWith("+91")) {
                    eTPhone.setText("+91");
                    eTPhone.setSelection(eTPhone.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dontAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        loginBtn.setOnClickListener(view -> {
            if (validateInputs()) {
                String phoneNumber = eTPhone.getText().toString().trim();
                loginBar.setVisibility(VISIBLE);
                loginBtn.setEnabled(false);
                loginBtn.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                checkUserExistsAndSendOtp(phoneNumber);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();


        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        initializeOtpCallbacks();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.google_sign_in_button).setOnClickListener(v -> signInWithGoogle());

    }

    private void initializeOtpCallbacks() {
        otpCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential,currentUserName);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loginBar.setVisibility(GONE);
                loginBtn.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this,R.color.colorPrimary));
                loginBtn.setEnabled(true);

                Toast.makeText(LoginActivity.this, "OTP Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                loginBar.setVisibility(GONE);
                loginBtn.setEnabled(true);
                showOtpDialog(verificationId, currentUserName);
            }
        };
    }

    private void checkUserExistsAndSendOtp(String phoneNumber) {
        usersRef.orderByChild("phone").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String userName = userSnapshot.child("name").getValue(String.class);
                        sendOtp(phoneNumber, userName);
                        break;
                    }


                } else {
                    Toast.makeText(LoginActivity.this, "User does not exist. Please register.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String currentUserName;
    private void sendOtp(String phoneNumber,String userName) {
        currentUserName = userName;
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(otpCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential , String currentUserName) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user.isEmailVerified()){
                    usersRef.child(user.getUid()).child("emailVerified").setValue(true);
                    Toast.makeText(this, "Welcome back, " + currentUserName + "!", Toast.LENGTH_SHORT).show();
                    navigateToMain();

                } else {
                    Toast.makeText(LoginActivity.this, "Welcome back " + currentUserName + ", please verify your email.", Toast.LENGTH_LONG).show();
                    navigateToMain();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Invalid OTP.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        if (eTPhone.getText().toString().trim().length() != 13) {
            showError(eTPhone, "Enter a valid phone number with +91 prefix");
            return false;
        }
        return true;
    }

    private void showOtpDialog(String verificationId, String currentUserName) {
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
                    if (index == otpBoxes.length - 1 && s.length() == 1) {// Remove focus if last digit is entered
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
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode.toString());
            signInWithPhoneAuthCredential(credential, currentUserName);
            otpDialog.dismiss();
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken(), account.getDisplayName());
            } catch (ApiException e) {
                eTPhone.setError("Google sign-in failed: " + e.getMessage());
                vibrateView(eTPhone);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken, String userName) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    checkUserProfile(user.getUid(), userName);
                }
            } else {
                eTPhone.setError("Firebase authentication failed.");
                vibrateView(eTPhone);
            }
        });
    }

    private void checkUserProfile(String userId, String userName) {
        usersRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Toast.makeText(this, "Welcome back, " + userName + "!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            } else {
                Toast.makeText(this, "Hi " + userName + "! Please complete your registration.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void navigateToMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
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


