// HomeFragment.java
package com.example.economicgrowthapp.landingpage.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.economicgrowthapp.NewssActivity;
import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.landingpage.CardAdapter;
import com.example.economicgrowthapp.landingpage.GdpDataModel;
import com.example.economicgrowthapp.landingpage.InflationDataModel;
import com.example.economicgrowthapp.landingpage.UnemploymentDataModel;
import com.example.economicgrowthapp.landingpage.settingspage;
import com.example.economicgrowthapp.searchdata.searcherPage;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int SHIMMER_TIMEOUT = 10000; // 20 seconds
    private static final long FIRST_PAGE_DELAY = 100;
    private static final long REGULAR_PAGE_DELAY = 3500;
    private final Handler autoSwipeHandler = new Handler();
    String lastYear = "2022";
    private List<Object> dataList;
    private LinearLayout mainpage;
    private LineChart indicator_chart;
    private ViewPager2 viewPager;
    private CardAdapter cardAdapter;
    private ShimmerFrameLayout shimmerloading, mainpageshimmer;
    private TextView indicator_text, main_username, currentvaluetext, indicator_growthValue, appreciation_depreciation, increasedecreaseText;
    private double lastGdpValue, lastunemploymentratevalue;
    private double lastgdpgrowth, lastinflationGrowth, lastgdppercapita;
    private ImageView settingsbtn;
    private CardView cardView3;
    private String gdpAmount, unemploymentamount, gdpgrowth, inflationGrowth, gdpGrowthPercentage, lastgdppercapitaamount;
    private CardView increasedecreaseBackground;
    private boolean isShimmerTimeout = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Runnable autoSwipeRunnable;
    private int currentPage = 0;
    private ConstraintLayout nointernet, cardView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        dataList = new ArrayList<>();
        viewPager = rootView.findViewById(R.id.viewpagercarousel);
        indicator_text = rootView.findViewById(R.id.indicator_text);
        indicator_chart = rootView.findViewById(R.id.indicator_chart);
        main_username = rootView.findViewById(R.id.main_username);
        shimmerloading = rootView.findViewById(R.id.shimmerloading);
        nointernet = rootView.findViewById(R.id.nointernet);
        indicator_growthValue = rootView.findViewById(R.id.indicator_growthValue);
        currentvaluetext = rootView.findViewById(R.id.currentvaluetext);
        appreciation_depreciation = rootView.findViewById(R.id.appreciation_depreciation);
        increasedecreaseBackground = rootView.findViewById(R.id.increasedecreaseBackground);
        increasedecreaseText = rootView.findViewById(R.id.increasedecreaseText);
        settingsbtn = rootView.findViewById(R.id.settingsbtn);
        mainpageshimmer = rootView.findViewById(R.id.mainpageshimmer);
        mainpage = rootView.findViewById(R.id.mainpage);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        cardView2 = rootView.findViewById(R.id.cardView2);
        cardView3 = rootView.findViewById(R.id.cardView3);

        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

        String inflationTitle = "Inflation ";
        String inflationAmount = "$";
        inflationGrowth = decimalFormat.format(lastinflationGrowth);
        String inflationRevenueText = "Consumer Price Index ";

        if (Double.parseDouble(String.valueOf(lastinflationGrowth)) > 0) {
            increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
            increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.decreaseCDVred));
            indicator_growthValue.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
            increasedecreaseText.setText("DEFLATION");
        } else if (Double.parseDouble(String.valueOf(lastinflationGrowth)) < 0) {
            increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
            increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.increaseCDVGreen));
            indicator_growthValue.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
            increasedecreaseText.setText("INFLATION");
        } else if (Double.parseDouble(String.valueOf(lastinflationGrowth)) == 0) {
            increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
            increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.increaseCDVGreen));
            indicator_growthValue.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
            increasedecreaseText.setText("STABLE");
        }
        stopShimmerLoading();
        dataList.add(new InflationDataModel(inflationTitle, inflationAmount, inflationGrowth, inflationRevenueText));

        String gdpTitle = "GDP ";
        gdpAmount = decimalFormat.format(lastGdpValue);
        gdpGrowthPercentage = decimalFormat.format(lastgdpgrowth);
        String gdpRevenueText = "Gross Domestic Product ";

        if (Double.parseDouble(String.valueOf(lastgdpgrowth)) <= 0) {
            increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
            increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.decreaseCDVred));
            increasedecreaseText.setText("DECREASE");
        } else if (Double.parseDouble(String.valueOf(lastgdpgrowth)) > 0) {
            increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
            increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.increaseCDVGreen));
            increasedecreaseText.setText("INCREASE");
        }
        stopShimmerLoading();
        dataList.add(new GdpDataModel(gdpTitle, gdpAmount, gdpGrowthPercentage, gdpRevenueText));

        String unemploymentTitle = "Unemployment Rate";
        String unemploymentAmount = "%";
        String unemploymentGrowthPercentage = "-";
        String unemploymentRevenueText = "Unemployment Rate ";
        //   indicator_text.setText("Unemployment Rate");
        indicator_chart.setNoDataText("Refresh...");
        indicator_chart.setNoDataTextColor(Color.GREEN); // Change text color
        stopShimmerLoading();
        dataList.add(new UnemploymentDataModel(unemploymentTitle, unemploymentAmount, unemploymentGrowthPercentage, unemploymentRevenueText));

        startShimmerLoading();
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), searcherPage.class);

                // Apply transition animation
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "transition_card_to_searcher");

                startActivity(intent, options.toBundle());

                // Set a handler to check for shimmer timeout
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mainpageshimmer.getVisibility() == View.VISIBLE) {
                            isShimmerTimeout = true;
                            Toast.makeText(getActivity(), "no internet", Toast.LENGTH_LONG).show();
                        }
                    }
                }, SHIMMER_TIMEOUT);
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(getActivity(), NewssActivity.class);
               // startActivity(intent);
                Toast.makeText(getContext(), "News Temporarily Unavailable", Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        startBounceAnimation(main_username);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userEmail = firebaseUser.getEmail();
            String googleUsername = extractGoogleUsername(userEmail);
            String Username = googleUsername;
            main_username.setText("Hello, " + Username);
        } else {
            main_username.setText("Hello Today.");
        }
        startBounceAnimation(cardView2);

        settingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), settingspage.class);
                startActivity(intent);
            }
        });

        cardAdapter = new CardAdapter(dataList);
        viewPager.setAdapter(cardAdapter);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(3);
        autoSwipeRunnable = new Runnable() {
            @Override
            public void run() {
                currentPage++;
                if (currentPage >= dataList.size()) {
                    currentPage = 1; // Circular scrolling
                }
                viewPager.setCurrentItem(currentPage);
                autoSwipeHandler.postDelayed(this, REGULAR_PAGE_DELAY);
            }
        };
        // Schedule the first page change after a short delay
        autoSwipeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(currentPage);
                autoSwipeHandler.postDelayed(autoSwipeRunnable, REGULAR_PAGE_DELAY);
            }
        }, FIRST_PAGE_DELAY);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                Object selectedItem = dataList.get(position);

                if (selectedItem instanceof GdpDataModel) {
                    new FetchDataTasks().execute();
                    indicator_text.setText("Gross Domestic Product");
                    indicator_text.setTextSize(26);
                    gdpAmount = decimalFormat.format(lastGdpValue); // Update gdpAmount here
                    lastgdppercapitaamount = decimalFormat.format(lastgdppercapita); // Update gdpAmount here
                    gdpgrowth = decimalFormat.format(lastgdpgrowth); // Update gdpgrowth here
                    currentvaluetext.setText("$" + lastgdppercapitaamount + " Per Capita");
                    indicator_growthValue.setText(gdpgrowth + "%");
                    appreciation_depreciation.setText("National accounts: Current prices at US$");

                    if (Double.parseDouble(String.valueOf(lastgdpgrowth)) > 0) {
                        indicator_growthValue.setTextColor(Color.GREEN);
                        increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
                        increasedecreaseText.setText("INCREASE");
                        increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.increaseCDVGreen));

                    } else if (Double.parseDouble(String.valueOf(lastgdpgrowth)) <= 0) {
                        indicator_growthValue.setTextColor(Color.RED);
                        increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
                        increasedecreaseText.setText("DECREASE");
                        increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.decreaseCDVred));
                    }
                    // Update the amount for the GdpDataModel instance
                    GdpDataModel gdpDataModel = (GdpDataModel) selectedItem;
                    gdpDataModel.setAmount(gdpAmount);
                    gdpDataModel.setGrowthPercentage(gdpgrowth);
                    // Notify the adapter that data has changed
                    cardAdapter.notifyDataSetChanged();

                } else if (selectedItem instanceof InflationDataModel) {
                    // Show shimmer loading view
                    new FetchDataTasks().execute();
                    currentvaluetext.setText("");
                    indicator_text.setText("Inflation");
                    appreciation_depreciation.setText("Inflation, consumer prices (annual %)");
                    inflationGrowth = decimalFormat.format(lastinflationGrowth); // Update inflationgrowth here
                    indicator_growthValue.setText(inflationGrowth + "%");
                    if (Double.parseDouble(String.valueOf(lastinflationGrowth)) > 0) {
                        indicator_growthValue.setTextColor(Color.RED);
                        increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
                        increasedecreaseText.setText("INFLATION");
                        increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.decreaseCDVred));
                    } else if (Double.parseDouble(String.valueOf(lastinflationGrowth)) < 0) {
                        indicator_growthValue.setTextColor(Color.GREEN);
                        increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
                        increasedecreaseText.setText("DEFLATION");
                        increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.increaseCDVGreen));
                    } else if (Double.parseDouble(String.valueOf(lastinflationGrowth)) == 0) {
                        indicator_growthValue.setTextColor(Color.GREEN);
                        increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
                        increasedecreaseText.setText("STABLE");
                        increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.increaseCDVGreen));
                    }
                    InflationDataModel inflationDataModel = (InflationDataModel) selectedItem;
                    inflationDataModel.setGrowthPercentage(inflationGrowth); // Update the amount field
                    inflationDataModel.setAmount(""); // Update the amount field
                    cardAdapter.notifyDataSetChanged();

                } else if (selectedItem instanceof UnemploymentDataModel) {
                    indicator_text.setText("Unemployment Rate");
                    appreciation_depreciation.setText("Unemployment, total (% of total labor force) (national estimate)");
                    new FetchDataTasks().execute();
                    unemploymentamount = decimalFormat.format(lastunemploymentratevalue); // Update gdpAmount here
                    currentvaluetext.setText("");

                    if (Double.parseDouble(String.valueOf(unemploymentamount)) < 0) {
                        indicator_growthValue.setTextColor(Color.GREEN);
                        increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
                        increasedecreaseText.setText("DECREASE");
                        increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.increaseCDVGreen));
                        indicator_growthValue.setTextColor(Color.GREEN);
                        indicator_growthValue.setText(unemploymentamount + "%");

                    } else if (Double.parseDouble(String.valueOf(lastgdpgrowth)) >= 0) {
                        indicator_growthValue.setTextColor(Color.RED);
                        increasedecreaseBackground.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.increaseCDVlight));
                        increasedecreaseText.setText("INCREASE");
                        increasedecreaseText.setTextColor(ContextCompat.getColor(getContext(), R.color.decreaseCDVred));
                        indicator_growthValue.setTextColor(Color.RED);
                        indicator_growthValue.setText(unemploymentamount + "%");
                    }
                }
            }
        });
        loadData();
        return rootView;
    }

    private void loadData() {
        // Show shimmer loading animation
        startShimmerLoading();

        // Fetch data asynchronously
        new FetchDataTasks().execute();
    }

    private void refreshData() {
        startShimmerLoading();
        new FetchDataTasks().execute();
        //stopRefreshing();
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            stopRefreshing();
        }
    }

    private String extractGoogleUsername(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            String username = email.substring(0, atIndex);
            return username.replaceAll("\\d", ""); // Remove digits/numbers
        } else {
            // Handle the case where email doesn't contain '@'
            Log.e("ExtractUsername", "Invalid email format");
            return "";
        }
    }

    private double extractLastGdpValue(JSONObject data) {
        try {
            JSONObject countryData = data.getJSONArray("Data").getJSONObject(3); // Assuming data is an array of objects
            if (countryData.has(lastYear + " [YR" + lastYear + "]")) {
                return countryData.getDouble(lastYear + " [YR" + lastYear + "]");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0f;

    }

    private double extractlastgdppercapita(JSONObject data) {
        try {
            JSONObject countryData = data.getJSONArray("Data").getJSONObject(36);
            if (countryData.has(lastYear + " [YR" + lastYear + "]")) {
                return countryData.getDouble(lastYear + " [YR" + lastYear + "]");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    private double extractGrowthGdpValue(JSONObject data) {
        try {
            JSONObject countryData = data.getJSONArray("Data").getJSONObject(4); // Assuming data is an array of objects
            if (countryData.has(lastYear + " [YR" + lastYear + "]")) {
                return countryData.getDouble(lastYear + " [YR" + lastYear + "]");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    private void createLineGraphForGdp(JSONObject data) {
        try {
            JSONObject countryData = data.getJSONArray("Data").getJSONObject(4); // Assuming data is an array of objects
            LineChart lineChart = indicator_chart;
            List<Entry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            for (int i = 2017; i <= 2022; i++) {
                String year = Integer.toString(i);
                if (countryData.has(year + " [YR" + i + "]")) {
                    float value = (float) countryData.getInt(year + " [YR" + i + "]");
                    entries.add(new Entry(i - 2017, value));
                    labels.add(year);
                }
            }
            LineDataSet dataSet = new LineDataSet(entries, "");
            dataSet.setColor(Color.GREEN); // Set line color to blue
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);
            dataSet.setCircleColor(Color.RED);
            dataSet.setDrawCircleHole(true);
            dataSet.setCircleHoleRadius(2f);
            dataSet.setDrawValues(true);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.GRAY);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            // Customize X-axis
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Use years as labels
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setTextColor(Color.GRAY);
            xAxis.setTextSize(10f);
            xAxis.setLabelRotationAngle(1);
            xAxis.setLabelCount(labels.size(), true); // Adjust label count for better fit
            // Disable left Y-axis
            lineChart.getAxisRight().setEnabled(false);
            // Customize right Y-axis
            YAxis yAxis2 = lineChart.getAxisRight();
            yAxis2.setTextColor(Color.GRAY);
            yAxis2.setDrawGridLines(false);
            yAxis2.setGridColor(Color.GRAY);
            // Customize legend (remove legend)
            lineChart.getLegend().setEnabled(false);
            // Disable description
            lineChart.getDescription().setEnabled(false);
            // Enable pinch zoom
            lineChart.setPinchZoom(true);
            // Apply animations
            lineChart.animateY(1000); // Animate Y-axis
            lineChart.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private double extractubemplotmentrateValue(JSONObject data) {
        try {
            JSONObject countryData = data.getJSONArray("Data").getJSONObject(83); // Assuming data is an array of objects
            if (countryData.has(lastYear + " [YR" + lastYear + "]")) {
                return countryData.getDouble(lastYear + " [YR" + lastYear + "]");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    private void createlinegraphforUnemploymentRate(JSONObject data) {
        try {
            JSONObject countryData = data.getJSONArray("Data").getJSONObject(82); // Assuming data is an array of objects
            LineChart lineChart = indicator_chart;

            List<Entry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (int i = 2007; i <= 2022; i++) {
                String year = Integer.toString(i);
                if (countryData.has(year + " [YR" + i + "]") && !countryData.getString(year + " [YR" + i + "]").equals("..")) {
                    float value = Float.parseFloat(countryData.getString(year + " [YR" + i + "]"));
                    entries.add(new Entry(i - 2007, value));
                    labels.add(year);
                }
            }
            LineDataSet dataSet = new LineDataSet(entries, "");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Set line colors
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);
            dataSet.setCircleColor(Color.RED);
            dataSet.setDrawCircleHole(true);
            dataSet.setCircleHoleRadius(2f);
            dataSet.setDrawValues(true);

            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.GRAY);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
            // Customize X-axis
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    if (index >= 0 && index < labels.size()) {
                        return labels.get(index);
                    }
                    return ""; // Return empty string for invalid indices
                }
            });
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setTextColor(Color.GRAY);
            xAxis.setTextSize(10f);
            xAxis.setLabelRotationAngle(0);
            xAxis.setLabelCount(labels.size(), true); // Adjust label count for better fit
            // Disable left Y-axis
            lineChart.getAxisRight().setEnabled(false);
            // Customize right Y-axis
            YAxis yAxis2 = lineChart.getAxisRight();
            yAxis2.setTextColor(Color.GRAY);
            yAxis2.setDrawGridLines(false);
            yAxis2.setGridColor(Color.GRAY);
            // Customize legend (remove legend)
            lineChart.getLegend().setEnabled(false);
            // Disable description
            lineChart.getDescription().setEnabled(false);
            // Enable pinch zoom
            lineChart.setPinchZoom(true);
            // Apply animations
            lineChart.animateY(1000); // Animate Y-axis
            lineChart.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        stopShimmerLoading(); // Stop shimmer animation
    }

    private double extractInflationGrowthValue(JSONObject data) {
        try {
            JSONObject countryData = data.getJSONArray("Data").getJSONObject(50); // Assuming data is an array of objects
            if (countryData.has(lastYear + " [YR" + lastYear + "]")) {
                return countryData.getDouble(lastYear + " [YR" + lastYear + "]");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    private void createLineGraphForInflation(JSONObject data) {
        try {
            JSONObject countryData = data.getJSONArray("Data").getJSONObject(50); // Assuming data is an array of objects
            LineChart lineChart = indicator_chart;

            List<Entry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (int i = 2017; i <= 2022; i++) {
                String year = Integer.toString(i);
                if (countryData.has(year + " [YR" + i + "]")) {
                    float value = (float) countryData.getInt(year + " [YR" + i + "]");
                    entries.add(new Entry(i - 2017, value));
                    labels.add(year);
                }
            }
            LineDataSet dataSet = new LineDataSet(entries, "");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Set line colors
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);
            dataSet.setCircleColor(Color.RED);
            dataSet.setDrawCircleHole(true);
            dataSet.setCircleHoleRadius(2f);
            dataSet.setDrawValues(true);

            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.GRAY);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);

            // Customize X-axis
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    if (index >= 0 && index < labels.size()) {
                        return labels.get(index);
                    }
                    return ""; // Return empty string for invalid indices
                }
            });
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setTextColor(Color.GRAY);
            xAxis.setTextSize(10f);
            xAxis.setLabelRotationAngle(0);
            xAxis.setLabelCount(labels.size(), true); // Adjust label count for better fit
            lineChart.getAxisRight().setEnabled(false);
            lineChart.getAxisLeft().setEnabled(false);
            // Customize Y-axis
            YAxis yAxis = lineChart.getAxisLeft();
            yAxis.setTextColor(Color.GRAY);
            YAxis yAxis2 = lineChart.getAxisRight();
            yAxis2.setTextColor(Color.GRAY);
            yAxis2.setDrawGridLines(false);
            yAxis2.setGridColor(Color.GRAY);
            yAxis2.setGranularity(0.1f); // Set Y-axis step size
            // Customize legend (remove legend)
            lineChart.getLegend().setEnabled(false);
            // Disable description
            lineChart.getDescription().setEnabled(false);
            // Enable pinch zoom
            lineChart.setPinchZoom(true);
            // Apply animations
            lineChart.animateY(1000); // Animate Y-axis
            lineChart.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        shimmerloading.stopShimmer();
        shimmerloading.setVisibility(View.GONE);
    }

    private void startShimmerLoading() {
        mainpageshimmer.startShimmer();
        mainpageshimmer.setVisibility(View.VISIBLE);
        mainpage.setVisibility(View.INVISIBLE);
    }

    private void stopShimmerLoading() {
        mainpageshimmer.stopShimmer();
        mainpageshimmer.setVisibility(View.GONE);
        mainpage.setVisibility(View.VISIBLE);
        isShimmerTimeout = false;
    }

    @Override
    public void onDestroy() {
        new Handler().removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void startBounceAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleUp = new ScaleAnimation(1, 1.00f, 1, 1.00f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(3000);
        scaleUp.setRepeatCount(Animation.INFINITE); // Infinite repeat
        scaleUp.setRepeatMode(Animation.REVERSE);
        TranslateAnimation bounceUp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -0.05f);
        bounceUp.setDuration(500);
        bounceUp.setRepeatCount(Animation.INFINITE); // Infinite repeat
        bounceUp.setRepeatMode(Animation.REVERSE);
        animationSet.addAnimation(scaleUp);
        animationSet.addAnimation(bounceUp);
        cardView2.startAnimation(animationSet);
    }

    private class FetchDataTasks extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL("https://mubby03.github.io/jsons/agriculture.json"); // Replace with your API endpoint
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    inputStream.close();

                    String json = stringBuilder.toString();
                    return new JSONObject(json);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                createLineGraphForInflation(result);
                lastinflationGrowth = extractInflationGrowthValue(result);
                lastunemploymentratevalue = extractubemplotmentrateValue(result);
                lastGdpValue = extractLastGdpValue(result);
                createLineGraphForGdp(result);
                lastgdpgrowth = extractGrowthGdpValue(result);
                lastgdppercapita = extractlastgdppercapita(result);
                // Dismiss shimmer after 3 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isShimmerTimeout) {
                            stopShimmerLoading();
                        }
                    }
                }, 3000);
            } else {
                // Handle case when data fetching failed
                stopShimmerLoading(); // Stop shimmer animation
            }
        }
    }
}