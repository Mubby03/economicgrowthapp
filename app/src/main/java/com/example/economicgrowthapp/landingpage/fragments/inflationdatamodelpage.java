package com.example.economicgrowthapp.landingpage.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economicgrowthapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class inflationdatamodelpage extends AppCompatActivity {

    private TextView textViewIndicatorName, textViewPeriodicity, textViewAggregationMethod,
            textViewTopic, textViewLongDefinition, textViewStatisticalConcept, textViewLimitations, accounttext, indicatortext;
    private ShimmerFrameLayout shimmerview;
    private LinearLayout maindata;
    private LineChart linechart;
    private CollapsingToolbarLayout toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datamodelpage);

        textViewIndicatorName = findViewById(R.id.textViewIndicatorName);
        textViewPeriodicity = findViewById(R.id.textViewPeriodicity);
        textViewAggregationMethod = findViewById(R.id.textViewAggregationMethod);
        textViewTopic = findViewById(R.id.textViewTopic);
        textViewLongDefinition = findViewById(R.id.textViewLongDefinition);
        textViewStatisticalConcept = findViewById(R.id.textViewStatisticalConcept);
        textViewLimitations = findViewById(R.id.textViewLimitations);
        accounttext = findViewById(R.id.accounttext);
        shimmerview = findViewById(R.id.shimmerview);
        maindata = findViewById(R.id.maindata);
        indicatortext = findViewById(R.id.indicator_text);
        linechart = findViewById(R.id.linechart);
        toolbar = findViewById(R.id.collaspsingToolbar);

        toolbar.setTitle("INFLATION");
        //  toolbar.setCollapsedTitleTextColor(Color.GREEN);
        toolbar.setExpandedTitleTextSize(55);
        indicatortext.setText("INFLATION");
        toolbar.setExpandedTitleTextAppearance(R.style.ExpandedTitle);
        toolbar.setCollapsedTitleTextAppearance(R.style.CollapsedTitle);

// Inside your second activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String receivedText = extras.getString("inflationrate");
            if (receivedText != null) {
                accounttext.setText(receivedText + "%");
                if (Double.parseDouble(receivedText) <= 0) {
                    accounttext.setTextColor(Color.RED);
                }
            }
        }

        // Set up the shimmer effect
        shimmerview.startShimmer();
        shimmerview.setVisibility(View.VISIBLE);
        maindata.setVisibility(View.GONE);

        linechart.setNoDataText("Refresh..");
        linechart.setNoDataTextColor(Color.GREEN);
        new inflationdatamodelpage.FetchDataTaskSummary().execute();
        new FetchInflationRateDataTask().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerview.startShimmer();
    }

    @Override
    protected void onPause() {
        shimmerview.stopShimmer();
        maindata.setVisibility(View.VISIBLE);
        super.onPause();
    }

    private class FetchDataTaskSummary extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL("https://mubby03.github.io/jsons/worlddatadetails.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = connection.getInputStream();
                         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                        }

                        String json = stringBuilder.toString();
                        Log.d("FetchDataTaskSummary", "JSON Data: " + json);
                        return new JSONObject(json);
                    }
                } else {
                    Log.e("FetchDataTaskSummary", "HTTP Response Code: " + responseCode);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            if (jsonObject != null) {
                try {
                    JSONArray dataArray = jsonObject.getJSONArray("Data");

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);
                        String code = dataObject.optString("Code"); // Use "optString" to handle null values

                        if ("FP.CPI.TOTL.ZG".equals(code)) { // Replace with the desired Code value
                            String indicatorName = dataObject.optString("Indicator Name");
                            String periodicity = dataObject.optString("Periodicity");
                            String aggregationMethod = dataObject.optString("Aggregation method");
                            String topic = dataObject.optString("Topic");
                            String longDefinition = dataObject.optString("Long definition");
                            String statisticalConcept = dataObject.optString("Statistical concept and methodology");
                            String limitations = dataObject.optString("Limitations and exceptions");

                            // Apply styles to TextViews
                            setStyledText(textViewIndicatorName, "Indicator Name:", indicatorName);
                            setStyledText(textViewPeriodicity, "Periodicity:", periodicity);
                            setStyledText(textViewAggregationMethod, "Aggregation Method:", aggregationMethod);
                            setStyledText(textViewTopic, "Topic:", topic);
                            setStyledText(textViewLongDefinition, "Long Definition:", longDefinition);
                            setStyledText(textViewStatisticalConcept, "Statistical Concept:", statisticalConcept);
                            setStyledText(textViewLimitations, "Limitations:", limitations);

                            shimmerview.stopShimmer();
                            shimmerview.setVisibility(View.GONE);
                            maindata.setVisibility(View.VISIBLE);
                            break; // Stop the loop once you find the desired Code
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle case when jsonObject is null (error occurred)
                Log.e("FetchDataTaskSummary", "JSON Data is null");
                shimmerview.stopShimmer();
                shimmerview.setVisibility(View.GONE);
                maindata.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setStyledText(TextView textView, String title, String value) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        // Create a span for bold and larger text size
        SpannableString titleSpannable = new SpannableString(title);
        titleSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleSpannable.setSpan(new AbsoluteSizeSpan(18, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        titleSpannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.modelpagetextheading)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(titleSpannable);

        builder.append("\n" + "\n");
        builder.append(value);
        textView.setText(builder);
    }

    private class FetchInflationRateDataTask extends AsyncTask<Void, Void, JSONObject> {

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
            }
        }
    }

    private void createLineGraphForInflation(JSONObject data) {
        try {
            JSONObject countryData = data.getJSONArray("Data").getJSONObject(50); // Assuming data is an array of objects
            LineChart lineChart = linechart;

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
            dataSet.setDrawValues(false); // Hide value labels
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Enable Bézier curves

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);

            // Customize X-axis
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setEnabled(false); // Hide X-axis

            // Customize left Y-axis
            YAxis yAxis1 = lineChart.getAxisLeft();
            yAxis1.setEnabled(false); // Hide Y-axis

            // Customize right Y-axis
            YAxis yAxis2 = lineChart.getAxisRight();
            yAxis2.setEnabled(false); // Hide Y-axis

            // Customize legend (remove legend)
            Legend legend = lineChart.getLegend();
            legend.setEnabled(false); // Hide legend

            // Disable description
            Description description = lineChart.getDescription();
            description.setEnabled(false); // Hide description

            // Disable pinch zoom and scaling
            lineChart.setPinchZoom(false);
            lineChart.setScaleEnabled(false);
            // Apply animations
            lineChart.animateY(1000); // Animate Y-axis
            lineChart.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void backpresed(View view) {
        onBackPressed();
    }
}