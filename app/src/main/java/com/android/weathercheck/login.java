package com.android.weathercheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    TextView createaccount;
    EditText email,password;
    AppCompatButton loginbtn;
    TextView invalid;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // User already logged in, go to MainActivity directly
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish();  // finish LoginActivity so it can't come back
        }
        setContentView(R.layout.activity_login);
        //connecting the views and buttons
        createaccount= findViewById(R.id.account);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        loginbtn=findViewById(R.id.login);
        invalid=findViewById(R.id.invalid);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        //Navigating to creating a account
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), signup.class);
                startActivity(intent);
            }
        });
        //login button action
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invalid.setVisibility(View.GONE);
                String emailstr=email.getText().toString().trim();
                String passStr=password.getText().toString().trim();
                if(emailstr.isEmpty()||passStr.isEmpty()){
                    invalid.setVisibility(View.VISIBLE);
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginbtn.setEnabled(false);
                    if (isInternetAvailable(this)) {
                    mAuth.signInWithEmailAndPassword(emailstr, passStr)
                            .addOnCompleteListener(task -> {
                                progressBar.setVisibility(View.GONE);
                                loginbtn.setEnabled(true);
                                if (task.isSuccessful()) {
                                    Toast.makeText(login.this, "Login successful!", Toast.LENGTH_SHORT).show();SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.apply();

                                    startActivity(new Intent(login.this, MainActivity.class)); // After login, go to MainActivity
                                    finish();
                                } else {
                                    Toast.makeText(login.this, "wrong password or email", Toast.LENGTH_SHORT).show();
                                }
                            });}
                    else
                        Toast.makeText(login.this, "Internet not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
