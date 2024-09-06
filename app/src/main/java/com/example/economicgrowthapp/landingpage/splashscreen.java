package com.example.economicgrowthapp.landingpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.logins.login;
import com.example.economicgrowthapp.onBoardingPages.onBoardingPage;

public class splashscreen extends AppCompatActivity {
    //declear the vairiables

    private static final int SPLASH_SCREEN = 3500;

    Animation topAnim, bottomAnim;
    ImageView image;
    SharedPreferences onBoardingScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //full screen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashscreen);

        //animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //hooks
        image = findViewById(R.id.imageView);

        image.startAnimation(fadeInAnimation());
        // Adjust the animation duration
        int animationDuration = 100; // 1 second

        topAnim.setDuration(animationDuration);
        bottomAnim.setDuration(animationDuration);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);
                //if this is the firstz time opening that app, go to Onboarding Screen
                if (isFirstTime) {
                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime", false);
                    editor.commit();

                    Intent intent = new Intent(splashscreen.this, onBoardingPage.class);
                    startActivity(intent);
                    finish();

                } else {

                    Intent intent = new Intent(splashscreen.this, login.class);
                    startActivity(intent);
                     finish();
                    // for the transistion
                 //   Log.d(TAG, "run: animation started");
                   // Pair[] pairs = new Pair[1];
                   // pairs[0] = new Pair<View, String>(image, "logo_image");


                    //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(splashscreen.this, pairs);

                    //option.tobundle will carry the animation
                  //  startActivity(intent, options.toBundle());
                   // finish();

                }
            }
        }, SPLASH_SCREEN);
    }
    public Animation fadeInAnimation() {
        Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(100);

        return fadeIn;
    }

}