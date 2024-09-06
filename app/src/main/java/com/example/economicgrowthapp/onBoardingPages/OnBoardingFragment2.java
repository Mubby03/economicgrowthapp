package com.example.economicgrowthapp.onBoardingPages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.economicgrowthapp.R;

public class OnBoardingFragment2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding2, container, false);

        // Find the button in the fragment layout
        ImageView nextButton = root.findViewById(R.id.nextbtn2);

        // Set click listener for the button to navigate to the next fragment
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Pair<View, String>[] pairs = new Pair[1];
                //  pairs[0] = new Pair<>(nextButton, "next_btn");

                // Move to the next fragment when the button is clicked
                ViewPager2 viewPager2 = getActivity().findViewById(R.id.pager);
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }
        });


        return root;
    }
}
