package com.example.economicgrowthapp.dialogues;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.landingpage.settingspage;
import com.example.economicgrowthapp.logins.forgot_password;
import com.example.economicgrowthapp.logins.google_login;
import com.example.economicgrowthapp.logins.login;
import com.example.economicgrowthapp.logins.register;
import com.example.economicgrowthapp.searchdata.searched_page;

public class CustomDialog extends Dialog {

    private final Context context;

    public CustomDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogue_successupload);

        LottieAnimationView lottieAnimationView = findViewById(R.id.lottie_main);
        TextView messageTextView = findViewById(R.id.dialog_message);

        if (context instanceof login) {
            messageTextView.setText("Login successful");
            lottieAnimationView.setAnimation(R.raw.successlottie);
        } else if (context instanceof settingspage) {
            messageTextView.setText("Upload successful");
           // lottieAnimationView.setAnimation(R.raw.failedlottie);
            lottieAnimationView.setAnimation(R.raw.successlottie);
        }else if (context instanceof google_login) {
            messageTextView.setText("Upload successful");
           // lottieAnimationView.setAnimation(R.raw.failedlottie);
            lottieAnimationView.setAnimation(R.raw.successlottie);
        }else if (context instanceof forgot_password) {
            messageTextView.setText("Mail Sent!");
           // lottieAnimationView.setAnimation(R.raw.failedlottie);
            lottieAnimationView.setAnimation(R.raw.successlottie);
        }else if (context instanceof register) {
            messageTextView.setText("Registration Success!");
           // lottieAnimationView.setAnimation(R.raw.failedlottie);
            lottieAnimationView.setAnimation(R.raw.successlottie);
        }else if (context instanceof searched_page) {
        messageTextView.setText("CSV Downloaded!");
        // lottieAnimationView.setAnimation(R.raw.failedlottie);
        lottieAnimationView.setAnimation(R.raw.successlottie);
        }

        lottieAnimationView.playAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1400); // Auto dismiss after 2 seconds
    }

}
