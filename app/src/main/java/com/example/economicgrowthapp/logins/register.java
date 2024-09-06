package com.example.economicgrowthapp.logins;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.dialogues.CustomDialog;
import com.example.economicgrowthapp.dialogues.CustomDialogfail;
import com.example.economicgrowthapp.landingpage.landing_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;

public class register extends AppCompatActivity {

    //work on the shared preference today ca
    // use ommooo
    // and also, do the stroke for the gdp_selectors layout.

    private final Boolean exit = false;
    TextView text02;
    CardView signupButton;
    CardView sign_google;
    Button goToLogin;
    TextInputLayout confirm_password, regEmail, create_password;
    String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    ProgressBar progressBar;
    Timer timer;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //hooks
        text02 = findViewById(R.id.text02);
        goToLogin = findViewById(R.id.gotologin);
        regEmail = findViewById(R.id.reg_email);
        confirm_password = findViewById(R.id.confirm_password);
        signupButton = findViewById(R.id.signup_btn);
        regEmail = findViewById(R.id.reg_email);
        create_password = findViewById(R.id.create_password);
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressbarsignup);
        sign_google = findViewById(R.id.sign_google);

        sign_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), google_login.class);
                startActivity(intent);
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(register.this, login.class);
                Pair<View, String>[] signupPairs = new Pair[3];
                signupPairs[0] = new Pair<>(confirm_password.getEditText(), "password_tran");
                signupPairs[1] = new Pair<>(regEmail.getEditText(), "username_tran");
                signupPairs[2] = new Pair<>(signupButton, "button_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(register.this, signupPairs);
                startActivity(intent, options.toBundle());
                finish();
            }
        });

        String buttonText = "Already have one? Login";


        SpannableString spannableString = new SpannableString(buttonText);

        // Set the "Already have one?" text to black color
        int startIndex = 0;
        int endIndex = buttonText.indexOf("Login");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#90939A")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the "Login" text to green color
        int loginStartIndex = endIndex;
        int loginEndIndex = buttonText.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0166F6")), loginStartIndex, loginEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        goToLogin.setText(spannableString);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = regEmail.getEditText().getText().toString();
                String txt_password = create_password.getEditText().getText().toString();

                regEmail.addOnEditTextAttachedListener(new TextInputLayout.OnEditTextAttachedListener() {
                    @Override
                    public void onEditTextAttached(@NonNull TextInputLayout textInputLayout) {
                        if (regEmail.getEditText().toString().length() >= 4) {
                            regEmail.setErrorEnabled(false);
                        } else {
                            regEmail.setError("enter valid mail");
                        }
                    }
                    //
                });

                if (confirm_password.getEditText().getText().toString().equals(create_password.getEditText().getText().toString())) {
                    String confirmPassword = confirm_password.getEditText().getText().toString();
                    if (confirmPassword.length() >= 4) {
                        confirm_password.setErrorEnabled(false);
                    } else {
                        confirm_password.getEditText().setError("Password must be at least 4 characters long");
                    }
                } else {
                    confirm_password.getEditText().setError("Password doesn't match");
                }
                if (TextUtils.isEmpty(txt_email)) {
                    regEmail.getEditText().setError("Enter your e-mail");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()) {
                    regEmail.getEditText().setError("Invalid email format");
                } else if (TextUtils.isEmpty(txt_password)) {
                    confirm_password.getEditText().setError("Enter your password");
                } else if (!txt_password.matches(passwordPattern)) {
                    confirm_password.getEditText().setError("Password must have at least one capital letter, number, small letter, special character, and must be longer than 8 digits");
                } else {
                    registerUser(txt_email, txt_password);
                    // Add the method implementation for registerUser(txt_email, txt_password);
                }

            }


            private void registerUser(String email, String password) {
                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Create a new user entry in the database under the "user" node
                            FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getUid()).setValue(new User("", regEmail.getEditText().getText().toString()));

                            CustomDialog customDialog = new CustomDialog(register.this);
                            customDialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), login.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_animation_right, R.anim.slide_fron_left);
                                    finish();
                                }
                            }, 3000);
                            finish();
                        } else {
                            CustomDialogfail customDialogfail = new CustomDialogfail(register.this);
                            customDialogfail.show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Check if the current activity is the login activity
        if (this instanceof register) {
            // If it is the login activity, simply finish the activity
            finish();
        } else {
            // If it is not the login activity, navigate back using the default behavior
            super.onBackPressed();
        }
    }
}