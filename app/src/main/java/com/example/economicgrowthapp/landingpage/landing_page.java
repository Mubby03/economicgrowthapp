package com.example.economicgrowthapp.landingpage;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.landingpage.fragments.HomeFragment;
import com.example.economicgrowthapp.landingpage.fragments.macrodataFragment;
import com.example.economicgrowthapp.landingpage.fragments.statedataFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class landing_page extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private LandingPageAdapter pagerAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landingpage);

        viewPager2 = findViewById(R.id.pager);
        pagerAdapter = new LandingPageAdapter(this);
        viewPager2.setAdapter(pagerAdapter);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = 0;
                if (item.getItemId() == R.id.bottom_nav_home) {
                    position = 1;
                } else if (item.getItemId() == R.id.bottom_nav_macrocalc) {
                    position = 0;
                } else if (item.getItemId() == R.id.bottom_nav_macrodata) {
                    position = 2;
                }
                viewPager2.setCurrentItem(position, false); // Disable smooth scrolling
                return true;
            }

        });

        // Set the initial current item to the middle position
        int middlePosition = pagerAdapter.getItemCount() / 2;
        viewPager2.setCurrentItem(middlePosition, false);

        // Disable swiping for ViewPager2
        viewPager2.setUserInputEnabled(false);

        // Set the default item in BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home); // Set to middle fragment
    }

    private class LandingPageAdapter extends FragmentStateAdapter {
        public LandingPageAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                    return new HomeFragment();
                case 0:
                    return new macrodataFragment();
                case 2:
                    return new statedataFragment();
                default:
                    return new HomeFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
