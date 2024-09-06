package com.example.economicgrowthapp.searchdata;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.chatbot.ChatbotBottomSheetDialogFragment;
import com.example.economicgrowthapp.dialogues.CustomDialogfail;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class searched_page extends AppCompatActivity {
    private static final String BOTTOM_SHEET_STATE = "bottomSheetState";
    private final String selectedTimeFrame = "3 Years";
    private final boolean isMoreInfoVisible = false;
    private final boolean islinegraphVisible = false;
    private final boolean isShimmerTimeout = false;
    int spinnerdefault = 63;
    ConstraintLayout askBot;
    private ImageView backbtn;
    private TextView indicatorandCOuntrytext, longdefinitiontext, periodicityamdaggmethod, statisticalConcept, showmoreBtn;
    private LinearLayout moreinfocardview, mainlayout;
    private ConstraintLayout linegraphContainer;
    private LineChart lineChart;
    private ProgressBar progress_bar;
    private Spinner startYearSpinner, endYearSpinner;
    private Button updateDataButton;
    private BarChart barChart;
    private ShimmerFrameLayout shimmerloader;
    private String allData = "";
    private String indicatorandCountrytext = "";
    private boolean isBottomSheetVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searched_page);
        backbtn = findViewById(R.id.backbtn);
        indicatorandCOuntrytext = findViewById(R.id.indicatorandCOuntrytext);
        longdefinitiontext = findViewById(R.id.longdefinitiontext);
        periodicityamdaggmethod = findViewById(R.id.periodicityamdaggmethod);
        statisticalConcept = findViewById(R.id.statisticalConcept);
        showmoreBtn = findViewById(R.id.showmoreBtn);
        linegraphContainer = findViewById(R.id.linegraphContainer);
        lineChart = findViewById(R.id.lineChartsearched);
        moreinfocardview = findViewById(R.id.moreinfocardview);
        progress_bar = findViewById(R.id.progress_bar);
        startYearSpinner = findViewById(R.id.start_year_spinner);
        endYearSpinner = findViewById(R.id.end_year_spinner);
        updateDataButton = findViewById(R.id.update_data_button);
        shimmerloader = findViewById(R.id.shimmerloader);
        barChart = findViewById(R.id.bar_chart);
        mainlayout = findViewById(R.id.mainlayout);
        askBot = findViewById(R.id.askBot);


        // Check if BottomSheet was visible before
        if (savedInstanceState != null) {
            isBottomSheetVisible = savedInstanceState.getBoolean(BOTTOM_SHEET_STATE);
        }

        // Check and show BottomSheet if it was visible before
        if (isBottomSheetVisible) {
            showChatbot();
        }


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.years_array, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startYearSpinner.setAdapter(adapter);
        endYearSpinner.setAdapter(adapter);
        int defaultIndex = 43; // Index of the item you want to be default
        startYearSpinner.setSelection(defaultIndex);

        askBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the strings from the UI components
                String longdef = longdefinitiontext.getText().toString().trim();
                String period = periodicityamdaggmethod.getText().toString().trim();
                String statCondept = statisticalConcept.getText().toString().trim();
                String indicatorandCountrytext = indicatorandCOuntrytext.getText().toString().trim();

                // String to be parsed to the ChatbotBottomSheetDialogFragment
                String searched_message1 = "Long Definition: " + longdef + "\n"
                        + "Periodicity and Aggregation Method: " + period + "\n"
                        + "Statistical Concept: " + statCondept + "\n"
                        + "All Data: " + allData;

                String searched_message = "Name of the indicator " + indicatorandCountrytext + "All Data: " + allData;


                // Create a bundle to pass data to the bottom sheet
                Bundle bundle = new Bundle();
                bundle.putString("searched_message", searched_message);

                // Create an instance of the bottom sheet fragment
                ChatbotBottomSheetDialogFragment bottomSheet = new ChatbotBottomSheetDialogFragment();
                bottomSheet.setArguments(bundle);

                // Show the bottom sheet
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(searched_page.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101);
            }
        }
        String seriesName = getIntent().getStringExtra("SeriesName");
        String seriesCode = getIntent().getStringExtra("SeriesCode");
        barChart.setVisibility(View.INVISIBLE);
        moreinfocardview.setVisibility(View.GONE);
// Set up a click listener for the button
        updateDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGraph();
            }
        });
        endYearSpinner.setSelection(spinnerdefault);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        showmoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreinfocardview.setVisibility(View.VISIBLE);
                showmoreBtn.setVisibility(View.GONE);
            }
        });

        linegraphContainer.setVisibility(View.VISIBLE);
        indicatorandCOuntrytext.setText(seriesName + ", Nigeria");
        indicatorandCountrytext = String.valueOf(indicatorandCOuntrytext);
        String startYear = startYearSpinner.getSelectedItem().toString();
        String endyear = endYearSpinner.getSelectedItem().toString();
        new FetchSelectedcodeDataTask(startYear, endyear).execute();
    }

    private void showChatbot() {
        ChatbotBottomSheetDialogFragment bottomSheetDialogFragment = new ChatbotBottomSheetDialogFragment();
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    private void displayAdditionalInfo(JSONObject data) {
        try {
            JSONArray seriesMetadata = data.getJSONArray("Series - Metadata");
            String selectedSeriesCode = getIntent().getStringExtra("SelectedSeriesCode");

            JSONObject selectedObject = null;
            for (int i = 0; i < seriesMetadata.length(); i++) {
                JSONObject metadata = seriesMetadata.getJSONObject(i);
                String code = metadata.optString("Code");
                if (code.equals(selectedSeriesCode)) {
                    selectedObject = metadata;
                    break;
                }
            }

            if (selectedObject != null) {
                String longDefinition = selectedObject.optString("Long definition");
                String periodicity = selectedObject.optString("Periodicity");
                String statisticalConcept1 = selectedObject.optString("Statistical concept and methodology");

                // Update TextViews with the retrieved information
                longdefinitiontext.setText("Long Definition: " + longDefinition);
                periodicityamdaggmethod.setText("Periodicity: " + periodicity);
                statisticalConcept.setText("Statistical Concept: " + statisticalConcept1);
            } else {
                Toast.makeText(searched_page.this, "Selected series code not found in metadata.", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createLineGraphForSelectedItem(JSONObject data) {
        try {
            JSONArray dataArray = data.getJSONArray("Data");
            String selectedSeriesCode = getIntent().getStringExtra("SelectedSeriesCode");

            JSONObject selectedObject = null;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject countryData = dataArray.getJSONObject(i);
                String seriesCode = countryData.optString("Series Code");
                if (seriesCode.equals(selectedSeriesCode)) {
                    selectedObject = countryData;
                    break;
                }
            }
            Log.d(TAG, "createLineGraphForSelectedItem: checking for graph data ");

            if (selectedObject != null) {
                List<String> availableYears = getAvailableYears(selectedObject);

                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                int startYear = Integer.parseInt(startYearSpinner.getSelectedItem().toString());
                int endYear = Integer.parseInt(endYearSpinner.getSelectedItem().toString());

                for (int i = startYear; i <= endYear; i++) {
                    String year = Integer.toString(i);
                    if (availableYears.contains(year)) {
                        String valueString = selectedObject.optString(year + " [YR" + year + "]");
                        double value;
                        try {
                            value = Double.parseDouble(valueString);
                            entries.add(new Entry(i - endYear, (float) value));
                            labels.add(year);
                        } catch (NumberFormatException e) {
                            // Handle the case where the value is not a valid number (optional)
                        }
                    }
                }
                // Inside createLineGraphForSelectedItem method
// Create entries for the bar chart
                List<BarEntry> barEntries = new ArrayList<>();
                for (int i = 0; i < entries.size(); i++) {
                    float value = entries.get(i).getY(); // Get the Y value from line chart entries
                    barEntries.add(new BarEntry(i, value));
                }
                BarDataSet barDataSet = new BarDataSet(barEntries, "Bar Data");
                barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
// Customize the bar chart
                barChart.getDescription().setEnabled(false); // Disable chart description
                barChart.setDrawValueAboveBar(true); // Display values above bars
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // X-axis position
                barChart.getXAxis().setDrawGridLines(false); // Disable vertical grid lines
                barChart.getAxisLeft().setDrawGridLines(false); // Disable horizontal grid lines
                barChart.getAxisRight().setEnabled(false); // Disable right y-axis
                barChart.getAxisLeft().setEnabled(true); // Enable left y-axis
                barChart.getAxisLeft().setGranularity(1f); // Set y-axis granularity
// Set value formatter for y-axis
                barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.format("%.1f", value);
                    }
                });

                barChart.setPinchZoom(false); // Disable pinch zoom
                barChart.animateY(1000); // Animate the bars
                barChart.invalidate(); // Refresh the bar chart
                barChart.invalidate(); // Refresh the bar chart

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
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f);
                xAxis.setTextColor(Color.GRAY);
                xAxis.setTextSize(8f);
                xAxis.setLabelRotationAngle(0);

                // Set the X-axis values
                xAxis.setLabelCount(labels.size(), true); // Adjust label count for better fit
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int year = endYear + (int) value; // Use the endYear variable
                        return String.valueOf(year);
                    }
                });

                lineChart.getAxisRight().setEnabled(true);
                lineChart.getAxisLeft().setEnabled(false);

                // Customize Y-axis
                YAxis yAxis = lineChart.getAxisLeft();
                yAxis.setTextColor(Color.GRAY);
                YAxis yAxis2 = lineChart.getAxisRight();
                yAxis2.setTextColor(Color.GRAY);
                yAxis2.setDrawGridLines(false);
                yAxis2.setGridColor(Color.GRAY);

// Set the Y-axis value formatter to display exact values without rounding
                yAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        // Convert the float value back to double
                        double doubleValue = value;
                        return String.valueOf(doubleValue);

                    }
                });

                // Customize legend (remove legend)
                lineChart.getLegend().setEnabled(false);
                lineChart.getDescription().setEnabled(false);
                lineChart.setPinchZoom(true);
                lineChart.animateY(1000); // Animate Y-axisx
                lineChart.invalidate();

                TableLayout tableLayout = findViewById(R.id.dataTable);
                StringBuilder allDataBuilder = new StringBuilder();

                for (String year : availableYears) {
                    TableRow row = new TableRow(this);

                    TextView yearTextView = new TextView(this);
                    yearTextView.setText(year);
                    row.addView(yearTextView);

                    String valueString = selectedObject.optString(year + " [YR" + year + "]");
                    double value;
                    try {
                        value = Double.parseDouble(valueString);
                    } catch (NumberFormatException e) {
                        // Handle the case where the value is not a valid number
                        value = Double.NaN; // Set value to NaN
                    }

                    String formattedValue;
                    if (Double.isNaN(value)) {
                        formattedValue = "-";
                    } else {
                        formattedValue = String.format("%.4f", value);
                    }

                    TextView valueTextView = new TextView(this);
                    valueTextView.setText(formattedValue);
                    row.addView(valueTextView);
                    tableLayout.addView(row);

                    // Append the cell data to the string with a delimiter
                    allDataBuilder.append(year).append(": ").append(formattedValue).append("\n");
                }

                // Remove the trailing newline character
                allData = allDataBuilder.toString().trim();
                if (allData.endsWith(", ")) {
                    allData = allData.substring(0, allData.length() - 2);
                }

// Display or further process the concatenated string
                Log.d(TAG, "All Data: " + allData);
                Log.d(TAG, "selectedSeriesCode: " + selectedSeriesCode);
                Log.d(TAG, "Available Years: " + availableYears);
            } else {
                Toast.makeText(this, "Selected series code not found.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getAvailableYears(JSONObject selectedObject) {
        List<String> availableYears = new ArrayList<>();
        if (selectedObject != null) {
            for (int i = 1980; i <= 2022; i++) {
                String year = Integer.toString(i);
                if (selectedObject.has(year + " [YR" + year + "]")) {
                    String valueString = selectedObject.optString(year + " [YR" + year + "]");
                    double value;
                    try {
                        value = Double.parseDouble(valueString);
                    } catch (NumberFormatException e) {
                        // Handle the case where the value is not a valid number
                        value = 0.0; // Or any default value you choose
                    }
                    availableYears.add(year);
                }
            }
        }
        return availableYears;
    }

    private void updateGraph() {
        String startYear = startYearSpinner.getSelectedItem().toString();
        String endYear = endYearSpinner.getSelectedItem().toString();

        // Fetch the data and update the graph based on the selected time frame
        new FetchSelectedcodeDataTask(startYear, endYear).execute();

        // Clear the table while keeping the header row
        TableLayout tableLayout = findViewById(R.id.dataTable);
        int childCount = tableLayout.getChildCount();
        tableLayout.removeViews(1, childCount - 1); // Remove all rows except the first one (header row)
    }

    public void showLineChart(View view) {
        barChart.setVisibility(View.INVISIBLE);
        lineChart.setVisibility(View.VISIBLE);
    }

    public void showBarChart(View view) {
        lineChart.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.VISIBLE);
    }

    public void exportCsv(View view) {
        try {
            TableLayout tableLayout = findViewById(R.id.dataTable);
            int rowCount = tableLayout.getChildCount();

            // Check if there are rows in the table
            if (rowCount <= 1) {
                Toast.makeText(this, "No data to export.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the text from the indicatorandCOuntrytext TextView
            String indicatorText = indicatorandCOuntrytext.getText().toString().trim();

            // Create a new file in the Downloads directory with a dynamic title
            File csvFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), indicatorText + ".csv");
            FileWriter csvWriter = new FileWriter(csvFile);

            // Write the header row
            TableRow headerRow = (TableRow) tableLayout.getChildAt(0);
            for (int j = 0; j < headerRow.getChildCount(); j++) {
                TextView textView = (TextView) headerRow.getChildAt(j);
                csvWriter.append(textView.getText().toString());
                if (j < headerRow.getChildCount() - 1) {
                    csvWriter.append(",");
                }
            }
            csvWriter.append("\n");

            // Write each row of data
            for (int i = 1; i < rowCount; i++) {
                TableRow row = (TableRow) tableLayout.getChildAt(i);
                for (int j = 0; j < row.getChildCount(); j++) {
                    TextView textView = (TextView) row.getChildAt(j);
                    csvWriter.append(textView.getText().toString());
                    if (j < row.getChildCount() - 1) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();

            // Toast.makeText(this, "CSV file exported successfully.", Toast.LENGTH_SHORT).show();
            //  CustomDialog customDialog = new CustomDialog(searched_page.this);
            // customDialog.show();
            downloadnotification();
        } catch (IOException e) {
            e.printStackTrace();
            // Toast.makeText(this, "Error exporting CSV file.", Toast.LENGTH_SHORT).show();
            CustomDialogfail customDialogfail = new CustomDialogfail(searched_page.this);
            customDialogfail.show();
        }
    }

    public void downloadnotification() {
        String indicatorText = indicatorandCOuntrytext.getText().toString().trim();
        String channelId = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.macrocon)
                .setContentTitle(indicatorText + "...")
                .setContentText("View Downloaded Content")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        intent.setDataAndType(uri, "*/*");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new
                        NotificationChannel(channelId, "Sone Description", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0, builder.build());
    }

    private class FetchSelectedcodeDataTask extends AsyncTask<String, Void, JSONObject> {
        private final String startYear;
        private final String endYear;

        public FetchSelectedcodeDataTask(String startYear, String endYear) {
            this.startYear = startYear;
            this.endYear = endYear;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progress_bar.setVisibility(View.VISIBLE);
            shimmerloader.setVisibility(View.VISIBLE);
            mainlayout.setVisibility(View.GONE);
            // Show progress bar before fetching data
        }

        @Override
        protected JSONObject doInBackground(String... years) {
            try {
                // You can use this.startYear and this.endYear to get the start and end years
                // Construct the URL based on startYear and endYear
                URL url = new URL("https://mubby03.github.io/jsons/nigeriadata.json"); // Replace with your API endpoint
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
            // progress_bar.setVisibility(View.GONE); // Hide progress bar after fetching data
            shimmerloader.setVisibility(View.GONE);
            mainlayout.setVisibility(View.VISIBLE);
            if (result != null) {
                createLineGraphForSelectedItem(result);
                displayAdditionalInfo(result);
            } else {
                Toast.makeText(searched_page.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}