package com.example.economicgrowthapp.calculators;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economicgrowthapp.R;
import com.example.economicgrowthapp.Retrofit.RetrofitBuilder;
import com.example.economicgrowthapp.Retrofit.RetrofitInterface;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class currencyExchangeActivity extends AppCompatActivity {

    Button button;
    EditText currencyToBeConverted;
    TextView currencyConverted, currency_name;
    Spinner convertToDropdown;
    Spinner convertFromDropdown;

    private List<String> xValues;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_exchange);
        //Initialization
        currencyConverted = findViewById(R.id.currency_converted);
        currencyToBeConverted = findViewById(R.id.currency_to_be_converted);
        convertToDropdown = findViewById(R.id.convert_to);
        convertFromDropdown = findViewById(R.id.convert_from);
        currency_name = findViewById(R.id.currency_name);
        button = findViewById(R.id.button);

        //functionality, adding to the spinners
        String[] dropdownList = {"USD", "AED", "AMD", "SZL", "ZWL", "NGN", "KWD", "AUD", "BDT", "CAD", "EUR", "GBP", "INR",
                "JPY", "MXN", "NZD", "SGD", "THB", "CNY", "HKD", "IDR", "MYR", "PHP", "RUB", "SEK", "TRY",
                "BRL", "CHF", "CZK", "DKK", "EGP", "HUF", "ILS", "KRW", "NOK", "PLN", "QAR", "SAR", "TWD",
                "VND", "ARS", "CLP", "COP", "ISK", "JOD", "KZT", "LBP", "MAD", "OMR", "PKR", "RON", "TND"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, dropdownList);
        convertToDropdown.setAdapter(adapter);
        convertFromDropdown.setAdapter(adapter);

        // Set button click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(currencyToBeConverted.getText().toString())) {
                    new FetchGraphDataTask().execute();
                    RetrofitInterface retrofitInterface = RetrofitBuilder.getRetrofitInstance().create(RetrofitInterface.class);
                    Call<JsonObject> call = retrofitInterface.getExchangeCurrency(convertFromDropdown.getSelectedItem().toString());
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject res = response.body();
                            JsonObject rates = res.getAsJsonObject("conversion_rates");
                            Double currency = Double.valueOf(currencyToBeConverted.getText().toString());
                            Double multiplier = Double.valueOf(rates.get(convertToDropdown.getSelectedItem().toString()).toString());
                            Double result = currency * multiplier;

                            // Format the result with decimal places and commas
                            DecimalFormat decimalFormat = new DecimalFormat("#,##0.000");

                            String formattedResult = decimalFormat.format(result);

                            currencyConverted.setText(formattedResult);
                            // Set the currency name
                            String selectedCurrency = convertToDropdown.getSelectedItem().toString();
                            currency_name.setText(selectedCurrency);
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                        }
                    });
                }
            }
        });

        // Disable button if EditText is empty
        currencyToBeConverted.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
        });
    }

    private class FetchGraphDataTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL("https://mubby03.github.io/jsons/currencyexchangeGraph.json");
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

                    String jsonData = stringBuilder.toString();
                    return new JSONObject(jsonData);
                } else {
                    throw new IOException("HTTP response code: " + responseCode);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                //  createLineChart(jsonObject);
            }
        }
    }
}
