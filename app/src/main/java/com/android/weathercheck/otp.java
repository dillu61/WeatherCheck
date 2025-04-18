package com.android.weathercheck;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class otp extends AppCompatActivity {

    private final String BASE_URL = "http://yourbackend.com";
    private final OkHttpClient client = new OkHttpClient();
    EditText otp;
    AppCompatButton verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        Intent intent=getIntent();
       String emailstr= intent.getStringExtra("email");
       sendOtpToEmail(emailstr);
       //linking with views
        otp=findViewById(R.id.otp);
        verify=findViewById(R.id.verify);
        //verfying otp
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpstr=otp.getText().toString().trim();
                if(otpstr.isEmpty())
                {
                    Toast.makeText(otp.this, "enter otp", Toast.LENGTH_SHORT).show();
                }
                else{
                    verifyOtp(emailstr, otpstr);
                }
            }
        });
    }
    private void sendOtpToEmail(String email) {
        String url = BASE_URL + "/send-otp?email=" + email;

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null)) // empty POST body
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(otp.this, "otp failed to send", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(otp.this, "otp send successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(otp.this, "otp failed to send", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void verifyOtp(String email, String otp) {
        String url = BASE_URL + "/verify-otp";

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->  Toast.makeText(otp.this, "verification failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(otp.this, "Otp verification successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        Toast.makeText(otp.this, "Invalid otp!", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}