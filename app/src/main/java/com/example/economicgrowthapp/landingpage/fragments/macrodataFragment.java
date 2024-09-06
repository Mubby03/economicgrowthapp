package com.example.economicgrowthapp.landingpage.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.calculators.currencyExchangeActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;

public class macrodataFragment extends Fragment {
    ShimmerFrameLayout mainView_shimmerView;
    private MaterialCardView currency_exchange,chatbot;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_macrodata, container, false);
        currency_exchange = rootView.findViewById(R.id.currency_exchange);
        mainView_shimmerView = rootView.findViewById(R.id.mainView_shimmerView);
        chatbot = rootView.findViewById(R.id.chatbot);
        currency_exchange.setVisibility(View.GONE);
        mainView_shimmerView.setVisibility(View.VISIBLE);
        mainView_shimmerView.startShimmer();

        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), com.example.economicgrowthapp.chatbot.chatbot.class);
                startActivity(intent);
            }
        });
// TODO: historical exchange rate feature
//  News and Market Analysis:
//  Integrate financial news and analysis to help users stay informed about global economic events and their impact on currencies.

        Handler handler = new Handler();
        handler.postDelayed(() -> {

            mainView_shimmerView.stopShimmer();
            mainView_shimmerView.setVisibility(View.GONE);
            currency_exchange.setVisibility(View.VISIBLE);
        }, 1500);

        currency_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), currencyExchangeActivity.class);
                startActivity(intent);
            }
        });


        return rootView;
    }
}