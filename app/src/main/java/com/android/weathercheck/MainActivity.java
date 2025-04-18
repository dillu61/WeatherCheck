package com.android.weathercheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText editTextSearch;
    TextView textViewCity, textViewDate, textViewWeatherDesc, textViewTemperature, textViewMaxTemp, textViewMinTemp;
    ImageView imageViewWeather;
    LinearLayout layoutMinMax;
    TextView textViewMessage;
    String apiKey = "af7957dd65d12bc3fcf25f5c4cc9870b";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editTextSearch = findViewById(R.id.editTextSearch);
        textViewCity = findViewById(R.id.textViewCity);
        textViewDate = findViewById(R.id.textViewDate);
        textViewWeatherDesc = findViewById(R.id.textViewWeatherDesc);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewMaxTemp = findViewById(R.id.textViewMaxTemp);
        textViewMinTemp = findViewById(R.id.textViewMinTemp);
        imageViewWeather = findViewById(R.id.imageViewWeather);
        layoutMinMax = findViewById(R.id.layoutMinMax);
         textViewMessage= findViewById(R.id.textViewMessage);
         ImageView logout=findViewById(R.id.logout);

        //search mechanism
        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String city = editTextSearch.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeatherData(city);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        //logout action
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

    }
    private void getWeatherData(String cityName) {


        Thread thread = new Thread(() -> {
            try {
                String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                        + cityName
                        + "&appid=" + apiKey
                        + "&units=metric";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(result.toString());

                String city = jsonObject.getString("name");
                String weatherDescription = jsonObject.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("description");
                String icon = jsonObject.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("icon");
                double temp = jsonObject.getJSONObject("main").getDouble("temp");
                double tempMax = jsonObject.getJSONObject("main").getDouble("temp_max");
                double tempMin = jsonObject.getJSONObject("main").getDouble("temp_min");

                runOnUiThread(() -> {
                    textViewCity.setText(city);
                    textViewWeatherDesc.setText(weatherDescription);
                    textViewTemperature.setText(String.format("%.0f°", temp));
                    textViewMaxTemp.setText(String.format("Max %.0f°", tempMax));
                    textViewMinTemp.setText(String.format("Min %.0f°", tempMin));
                    textViewMessage.setVisibility(View.GONE);

                    // Load weather icon
                    String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
                    Picasso.get().load(iconUrl).into(imageViewWeather);

                    // Set current date
                    textViewDate.setText(android.text.format.DateFormat.format("EEE, MMM d", new java.util.Date()));
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "City not found!", Toast.LENGTH_SHORT).show()
                );
            }
        });
        thread.start();
    }
    private void showAlertDialog() {
        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout?")
                .setIcon(android.R.drawable.ic_dialog_alert)  // You can change this icon
                .setCancelable(false)  // Makes the dialog non-cancellable (can't dismiss by tapping outside)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Action when "Yes" is clicked
                        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLoggedIn", false);
                        editor.apply();
                        Intent intent=new Intent(MainActivity.this,login.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Action when "No" is clicked
                        dialog.dismiss();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}