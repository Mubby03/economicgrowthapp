package com.example.economicgrowthapp.landingpage;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.landingpage.fragments.gdpdatamodelpage;
import com.example.economicgrowthapp.landingpage.fragments.inflationdatamodelpage;
import com.example.economicgrowthapp.landingpage.fragments.unemploymentdatamodelpage;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<Object> dataList;

    public CardAdapter(List<Object> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_itemview_cardview, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Object data = dataList.get(position);

        if (data instanceof InflationDataModel) {
            InflationDataModel inflationData = (InflationDataModel) data;
            holder.bindInflationData(inflationData);
        } else if (data instanceof GdpDataModel) {
            GdpDataModel gdpData = (GdpDataModel) data;
            holder.bindGdpData(gdpData);
        } else if (data instanceof UnemploymentDataModel) {
            UnemploymentDataModel unemploymentData = (UnemploymentDataModel) data;
            holder.bindUnemploymentData(unemploymentData);
        }

        // Set OnClickListener for the whole card view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event based on the data type
                if (data instanceof InflationDataModel) {
                    Intent intent = new Intent(view.getContext(), inflationdatamodelpage.class);
                    intent.putExtra("inflationrate", ((InflationDataModel) data).getGrowthPercentage());
                    view.getContext().startActivity(intent);

                } else if (data instanceof GdpDataModel) {
                    Intent intent = new Intent(view.getContext(), gdpdatamodelpage.class);
                    intent.putExtra("gdpamount", ((GdpDataModel) data).getAmount());
                    intent.putExtra("gdpamount2", ((GdpDataModel) data).getGrowthPercentage());
                    view.getContext().startActivity(intent);
                    //intent.putExtra("data", GdpDataModel);

                } else if (data instanceof UnemploymentDataModel) {
                    Intent intent = new Intent(view.getContext(), unemploymentdatamodelpage.class);
                  //  intent.putExtra("unemploymentrate", ((GdpDataModel) data).getAmount());
                    view.getContext().startActivity(intent);                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTextView;
        private final TextView amountTextView;
        private final TextView growthPercentageTextView;
        private final TextView revenueTextView;


        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textView5);
            amountTextView = itemView.findViewById(R.id.accounttext);
            growthPercentageTextView = itemView.findViewById(R.id.indicator_growthValue);
            revenueTextView = itemView.findViewById(R.id.currentvaluetext);

        }

        public void bindInflationData(InflationDataModel inflationData) {
            titleTextView.setText(inflationData.getTitle());
            amountTextView.setText("");


            double inflationgrowth = Double.parseDouble(inflationData.getGrowthPercentage());
            if (inflationgrowth >= 0) {
                growthPercentageTextView.setTextColor(Color.RED);
                growthPercentageTextView.setText("+" + inflationData.getGrowthPercentage() + "%");
                growthPercentageTextView.setTextSize(25);

            } else {
                growthPercentageTextView.setTextColor(Color.GREEN);
                growthPercentageTextView.setText("-" + inflationData.getGrowthPercentage() + "%");
                growthPercentageTextView.setTextSize(25);


            }
            //  growthPercentageTextView.setTextSize(12);
            revenueTextView.setText(inflationData.getRevenueText());
        }


        public void bindGdpData(GdpDataModel gdpData) {
            titleTextView.setText(gdpData.getTitle());
            amountTextView.setText("₦" + gdpData.getAmount());
            double gdppercentagegrowth = Double.parseDouble(gdpData.getGrowthPercentage());
            growthPercentageTextView.setTextSize(12);

            revenueTextView.setText(gdpData.getRevenueText());

            if (gdppercentagegrowth <= 0) {
                growthPercentageTextView.setTextColor(Color.RED);
                growthPercentageTextView.setText("-" + gdpData.getGrowthPercentage() + "%");
            } else {
                growthPercentageTextView.setTextColor(Color.GREEN);
                growthPercentageTextView.setText("+" + gdpData.getGrowthPercentage() + "%");
            }
        }


        public void bindUnemploymentData(UnemploymentDataModel unemploymentData) {
            titleTextView.setText(unemploymentData.getTitle());
            // amountTextView.setText(unemploymentData.getAmount());
            amountTextView.setText("");
            //   growthPercentageTextView.setText(unemploymentData.getGrowthPercentage());
            growthPercentageTextView.setText("No Data.");
            growthPercentageTextView.setTextSize(25);
            revenueTextView.setText(unemploymentData.getRevenueText());
        }
    }
}
