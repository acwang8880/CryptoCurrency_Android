package com.example.alex.crytoconv;

import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText fromText;
    TextView toText;
    Button convertButton;
    Button refreshButton;
    ProgressBar progressBar;
    TextView responseView;
    Spinner spinnerFrom;
    Spinner spinnerTo;

    final String API_URL = "https://min-api.cryptocompare.com/data/price?";
//    {USD: 1 "EUR":0.8065,"CAD":1.24,"BTC":0.00008842,"ETH":0.0008507,"XRP":0.7813,"LTC":0.005528}
    static double[] rates = new double[] {1.0, 0.8065, 1.24, 0.00008842, 0.0008507, 0.7813, 0.005528};
//    static double[] rates = new double[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromText = (EditText) findViewById(R.id.fromText);
        toText = (TextView) findViewById(R.id.toText);
        convertButton = (Button) findViewById(R.id.convertButton);
        refreshButton = (Button) findViewById(R.id.refreshButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        responseView = (TextView) findViewById(R.id.responseView);
        spinnerFrom = (Spinner) findViewById(R.id.spinner1);
        spinnerTo = (Spinner) findViewById(R.id.spinner2);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fromText.getText().toString().equals("")) {
                    Double fromVal = (Double) Double.parseDouble(fromText.getText().toString());
                    String fromCoin = (String) spinnerFrom.getSelectedItem().toString();
                    String toCoin = (String) spinnerTo.getSelectedItem().toString();
                    int fromIndex = (int) spinnerFrom.getSelectedItemPosition();
                    int toIndex = (int) spinnerTo.getSelectedItemPosition();

                    Toast.makeText(MainActivity.this,"From: " + fromCoin + "\n" +
                            "To: " + toCoin + " " + toIndex, Toast.LENGTH_SHORT).show();

                    Double result;
                    // Calc val fromCoin to USD
                    result = fromVal / rates[fromIndex];
                    // Calc val USD to toCoin
                    result = result * rates[toIndex];
                    // Assign result to toVal
                    result = Math.round(result * 100.0) / 100.0;
                    toText.setText(result.toString());

                } else {
                    Toast.makeText(MainActivity.this, "Empty \"From\" value.\nPut something there!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RetrieveFeedTask().execute();
            }
        });
    }
    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        @Override
        protected String doInBackground(Void... urls) {
            String fsym = "USD";
            String tsym = "EUR,CAD,BTC,ETH,XRP,LTC";

            try {
                URL url = new URL(API_URL + "fsym=" + fsym + "&" +
                        "tsyms=" + tsym);
                HttpURLConnection urlCOnnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlCOnnection.getInputStream()));
                    StringBuilder strBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] sample = line.split(",");
                        for (int i = 0; i < sample.length; i++) {
                            String[] coinEntry = sample[i].split(":");
                            Log.i("CoinRates from internet", coinEntry[0].replace("{", "") + " " + coinEntry[1].replace("}",""));
                            rates[i+1] = Double.parseDouble(coinEntry[1].replace("}",""));
                        }
                        strBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return strBuilder.toString();
                } finally {
                    urlCOnnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
//                StringBuilder what = new StringBuilder();
//
//                for (int i = 0; i < rates.length; i++) {
//                    what.append(rates[i] + " ");
//                }
//                response = what.toString();
//
                response = "Update Failed.";
            }
            progressBar.setVisibility(View.GONE);
//            responseView.setText(response);
            Toast.makeText(MainActivity.this, "Refreshed Rates!", Toast.LENGTH_SHORT).show();
        }
    }
}
