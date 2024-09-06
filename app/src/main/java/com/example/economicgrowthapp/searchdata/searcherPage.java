package com.example.economicgrowthapp.searchdata;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economicgrowthapp.R;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class searcherPage extends AppCompatActivity implements SearchAdapter.OnItemClickListener {
    private static final String JSON_URL = "https://mubby03.github.io/jsons/nigeriadata.json";

    private final List<WorldBankIndicator> indicatorList = new ArrayList<>();
    private final List<WorldBankIndicator> filteredList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText editTextSearch;
    private SearchAdapter adapter;
    private Button searchButton;
    private ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searcher_page);

        recyclerView = findViewById(R.id.recyclerViewResults);
        editTextSearch = findViewById(R.id.editTextSearch);
        searchButton = findViewById(R.id.buttonSearch);
        backbtn = findViewById(R.id.backbtn);

        adapter = new SearchAdapter(this, filteredList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(
                    TransitionInflater.from(this).inflateTransition(R.transition.slide_up));
            getWindow().setExitTransition(null);
        }

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use onBackPressed() to simulate the back button press
                onBackPressed();
            }
        });

        searchButton.setOnClickListener(view -> filterList(editTextSearch.getText().toString()));
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fetchDataFromRemoteURL();
    }

    private void filterList(String query) {
        filteredList.clear();
        String lowerCaseQuery = query.toLowerCase().trim();
        for (WorldBankIndicator indicator : indicatorList) {
            if (indicator != null && indicator.getSeriesName() != null) {
                String lowerCaseSeriesName = indicator.getSeriesName().toLowerCase().trim();
                if (lowerCaseSeriesName.contains(lowerCaseQuery)) {
                    filteredList.add(indicator);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void fetchDataFromRemoteURL() {
        new FetchDataTask().execute(JSON_URL);
    }


    @Override
    public void onItemClick(WorldBankIndicator indicator) {
        // Pass indicatorList to the searchpage activity
        Intent intent = new Intent(getApplicationContext(), searched_page.class);
        Log.d(TAG, "onItemClick: for searcher page started ");
        intent.putExtra("CountryName", indicator.getCountryName());
        intent.putExtra("CountryCode", indicator.getCountryCode());
        intent.putExtra("SeriesName", indicator.getSeriesName());
        intent.putExtra("SeriesCode", indicator.getSeriesCode());
        intent.putExtra("IndicatorList", new ArrayList<>(indicatorList));
        intent.putExtra("SelectedSeriesCode", indicator.getSeriesCode());
        startActivity(intent);
    }


    private class FetchDataTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
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
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    JSONArray data = jsonObject.getJSONArray("Data");
                    indicatorList.clear();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject indicatorData = data.getJSONObject(i);
                        String seriesName = indicatorData.optString("Series Name");
                        String countryCode = indicatorData.optString("Country Code");
                        String seriesCode = indicatorData.optString("Series Code");

                        WorldBankIndicator indicator = new WorldBankIndicator();
                        indicator.setSeriesName(seriesName);
                        indicator.setCountryCode(countryCode);
                        indicator.setSeriesCode(seriesCode);

                        // Extract year data here if needed
                        extractYearData(indicator, indicatorData);

                        indicatorList.add(indicator);
                    }

                    filterList(editTextSearch.getText().toString());
                    // Log the extracted data
                    for (WorldBankIndicator indicator : indicatorList) {

                        Log.d("MainActivity", "Series Name: " + indicator.getSeriesName());
                        Log.d("MainActivity", "Country Code: " + indicator.getCountryCode());
                        Log.d("MainActivity", "Series Code: " + indicator.getSeriesCode());

                        // Log year data if needed
                        Map<String, Double> yearData = indicator.getYearData();
                        if (yearData != null) {
                            for (Map.Entry<String, Double> entry : yearData.entrySet()) {
                                Log.d("MainActivity", "Year: " + entry.getKey() + ", Value: " + entry.getValue());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(searcherPage.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        }

        private void extractYearData(WorldBankIndicator indicator, JSONObject indicatorData) {
            Map<String, Double> yearData = indicator.getYearData();
            if (yearData == null) {
                // Initialize the year data map
                yearData = new HashMap<>();
                indicator.setYearData(yearData);
            }

            // Extract year data for each year and add it to the map
            for (int year = 2003; year <= 2022; year++) {
                String yearKey = String.valueOf(year);
                if (indicatorData.has(yearKey)) {
                    try {
                        double yearValue = indicatorData.getDouble(yearKey);
                        yearData.put(yearKey, yearValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
