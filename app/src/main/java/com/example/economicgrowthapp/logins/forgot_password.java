package com.example.economicgrowthapp.logins;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
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
import com.google.firebase.auth.FirebaseAuth;

public class forgot_password extends AppCompatActivity {
    private TextInputLayout resetpasswordedittext;
    private CardView reset_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetpasswordedittext = findViewById(R.id.resetpasswordedittext);
        reset_button = findViewById(R.id.reset_button);

        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = resetpasswordedittext.getEditText().getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                CustomDialog customDialog = new CustomDialog(forgot_password.this);
                                customDialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 3000);
                //Intent go to login
                            } else {
                                CustomDialogfail customDialogfail = new CustomDialogfail(forgot_password.this);
                                customDialogfail.show();
                                // Show the custom dialog on failure
                                resetpasswordedittext.setError("email was not sent");
                                resetpasswordedittext.setErrorEnabled(true);
                            }
                        }
                    });
                } else {
                    resetpasswordedittext.setError("enter email");
                    resetpasswordedittext.setErrorEnabled(true);
                }
            }
        });

    }
}