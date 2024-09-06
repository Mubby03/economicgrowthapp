package com.example.economicgrowthapp.landingpage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economicgrowthapp.Models.CustomAdapter;
import com.example.economicgrowthapp.R;

public class statedataFragment extends Fragment {
    RecyclerView recyclerView;
    CustomAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statedata, container, false);


        return rootView;
    }


}
