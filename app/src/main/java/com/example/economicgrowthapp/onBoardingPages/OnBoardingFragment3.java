package com.example.economicgrowthapp.onBoardingPages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.logins.login;

public class OnBoardingFragment3 extends Fragment {
    private CardView ButtonOnboarding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding3, container, false);

        ButtonOnboarding = root.findViewById(R.id.onBoardingLogin);
        ButtonOnboarding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return root;
    }
}
