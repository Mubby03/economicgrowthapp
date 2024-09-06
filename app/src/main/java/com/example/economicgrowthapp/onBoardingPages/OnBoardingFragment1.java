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

public class OnBoardingFragment1 extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding1, container,false);

        ImageView nextButton = root.findViewById(R.id.nextbtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager2 viewPager2 = getActivity().findViewById(R.id.pager);
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);



            }
        });



       return root;



    }

}
