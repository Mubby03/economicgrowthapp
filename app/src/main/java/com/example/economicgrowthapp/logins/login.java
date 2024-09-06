package com.example.economicgrowthapp.logins;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.economicgrowthapp.landingpage.settingspage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class login extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    CardView login_btn, google_btn;
    TextView logoText, sloganText;
    TextInputLayout email1, password1;
    private Button gotoregister, forgot_password;
    private FirebaseAuth auth;
    private CheckBox rememberMe;
    private ProgressBar progressBar;
    private Boolean exit = false;
    private TextInputEditText username_edit_text, password_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Hooks
        gotoregister = findViewById(R.id.gotosignup);
        logoText = findViewById(R.id.logo_name);
        google_btn = findViewById(R.id.googlestuff);
        login_btn = findViewById(R.id.login_btn);
        email1 = findViewById(R.id.reg_username);
        password1 = findViewById(R.id.confirm_password);
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progress_bar);
        forgot_password = findViewById(R.id.forgot_password);
        rememberMe = findViewById(R.id.rememberMe);
        username_edit_text = findViewById(R.id.username_edit_text);
        password_edit_text = findViewById(R.id.password_edit_text);

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_success, null);
        TextView successMessage = dialogView.findViewById(R.id.success_message);
        successMessage.setText("Login Success");
        builder.setView(dialogView);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

         */
        checkBox();

        SessionManager sessionManager = new SessionManager(getApplicationContext(), SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeFromSession();
            username_edit_text.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONEMAIL));
            password_edit_text.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));

        }

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), com.example.economicgrowthapp.logins.forgot_password.class);
                startActivity(intent);
            }
        });

        String buttonText = "Don't have one? Create an account";

        SpannableString spannableString = new SpannableString(buttonText);

        // Set the "Already have one?" text to black color
        int startIndex = 0;
        int endIndex = buttonText.indexOf("Create an account");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#90939A")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the "Login" text to green color
        int loginStartIndex = endIndex;
        int loginEndIndex = buttonText.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0166F6")), loginStartIndex, loginEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        gotoregister.setText(spannableString);

        gotoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, register.class);

                Pair<View, String>[] pairs = new Pair[4];

                pairs[0] = new Pair<>(email1.getEditText(), "username_tran");
                pairs[1] = new Pair<>(password1.getEditText(), "password_tran");
                pairs[2] = new Pair<>(login_btn, "button_tran");
                pairs[3] = new Pair<>(gotoregister, "login_signup_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(login.this, pairs);
                startActivity(intent, options.toBundle());

            }
        });

        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), google_login.class);
                startActivity(intent);
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email1.getEditText().getText().toString();
                String txt_password = password1.getEditText().getText().toString();
                loginUser(txt_email, txt_password);
            }

            private void loginUser(String email, String password) {
                if (TextUtils.isEmpty(email)) {

                    email1.setError("enter email");
                    return;
                } else {
                    email1.setError(null);
                    email1.setErrorEnabled(false);

                }
                if (TextUtils.isEmpty(password)) {

                    password1.setError("enter password");
                    return;
                } else {
                    password1.setError(null);
                    password1.setErrorEnabled(false);
                }
                progressBar.setVisibility(View.VISIBLE);


                if (rememberMe.isChecked()) {


                }
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            CustomDialog customDialog = new CustomDialog(login.this);
                            customDialog.show();
                            // for the autoLogin shared preference
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name", "true");
                            editor.apply();

                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String userEmail = user.getEmail();

                                String userPassword = password;
                                if (rememberMe.isChecked()) {

                                    //this is to store the user's data locally, including the password and the email.
                                    SessionManager sessionManager = new SessionManager(getApplicationContext(), SessionManager.SESSION_REMEMBERME);
                                    sessionManager.createRememberMeSession(userEmail, password);
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), landing_page.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 3000);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), landing_page.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 3000);
                        } else {
                            CustomDialogfail customDialogfail = new CustomDialogfail(login.this);
                            customDialogfail.show();
                            // Show the custom dialog on failure
                        }
                    }
                });
            }
        });

    }

    private void checkBox() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean isGoogleLogin = sharedPreferences.getBoolean("google_login", false);
        if (isGoogleLogin) {
            CustomDialog customDialog = new CustomDialog(login.this);
            customDialog.show();
            // for the autoLogin shared preference
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), landing_page.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        } else {
            String check = sharedPreferences.getString("name", "");
            if (check.equals("true")) {
                Intent intent = new Intent(getApplicationContext(), landing_page.class);
                startActivity(intent);
                finish();
            }
        }
    }
    //Get user's Data from FireBase
    //create a session

    @Override
    public void onBackPressed() {
        // Check if the current activity is the login activity
        if (this instanceof login && exit) {
            // If it is the login activity, simply finish the activity
            finish();
        } else {
            // If it is not the login activity, navigate back using the default behavior
            super.onBackPressed();
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

}