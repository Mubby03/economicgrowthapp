package com.example.economicgrowthapp.onBoardingPages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.logins.login;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class onBoardingPage extends AppCompatActivity {
    private static final int NUM_PAGES = 3;
    private ViewPager2 viewPager2;
    private ScreenSlidePageAdapter pagerAdapter;
    Animation animate;
    ConstraintLayout congostface, constraintLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewPager2 = findViewById(R.id.pager);
        constraintLayout2 = findViewById(R.id.constraintLayout2);
        pagerAdapter = new ScreenSlidePageAdapter(this);
        viewPager2.setAdapter(pagerAdapter);

        // Set up the TabLayout with the ViewPager2
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            // Set tab text here if needed (optional)
            // For example, you can set tab text to "Page 1", "Page 2", etc.:
            // tab.setText("Page " + (position + 1));
        }).attach();
     //   loginbtn = findViewById(R.id.onBoardingLogin);

        congostface = findViewById(R.id.congostface);

        animate = AnimationUtils.loadAnimation(this, R.anim.animmmhehe);

        viewPager2.setAnimation(animate);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Get the ConstraintLayout2 view
                ConstraintLayout constraintLayout2 = findViewById(R.id.constraintLayout2);
                // Animate the view to slide down and become invisible
                constraintLayout2.animate()
                        .alpha(0.0f) // Gradually fade out the view by setting alpha to 0
                        .setDuration(400) // Set the duration of the animation to 400 milliseconds
                        .setStartDelay(10) // Set a start delay of 10 milliseconds
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                constraintLayout2.setVisibility(View.INVISIBLE); // Set the visibility to invisible after the animation completes
                            }
                        })
                        .start();
                congostface.animate()
                        .alpha(0.0f) // Gradually fade out the view by setting alpha to 0
                        .setDuration(300) // Set the duration of the animation to 400 milliseconds
                        .setStartDelay(10) // Set a start delay of 10 milliseconds
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                congostface.setVisibility(View.INVISIBLE); // Set the visibility of the view to invisible after the animation completes
                            }
                        })
                        .start();
            }
        }, 720);

    }

    public void skipFunction(View view) {
        startActivity(new Intent(getApplicationContext(), login.class));
        finish();
    }


    private class ScreenSlidePageAdapter extends FragmentStateAdapter {

        public ScreenSlidePageAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    OnBoardingFragment1 tab1 = new OnBoardingFragment1();

                    return tab1;
                case 1:
                    OnBoardingFragment2 tab2 = new OnBoardingFragment2();
                    return tab2;
                case 2:
                    OnBoardingFragment3 tab3 = new OnBoardingFragment3();
                    return tab3;
                //default:
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}