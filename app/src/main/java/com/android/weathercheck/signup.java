package com.android.weathercheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class signup extends AppCompatActivity {
    TextView login,mismatch;
    AppCompatButton create;
    EditText email,pass,repass;
    String emailStr,passStr,repassStr;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        //connecting the views and button
        login=findViewById(R.id.login);
        mismatch=findViewById(R.id.mismatch);
        create=findViewById(R.id.create);
        email=findViewById(R.id.email);
        pass=findViewById(R.id.password);
        repass=findViewById(R.id.repass);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        //on clicking create account
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mismatch.setVisibility(View.GONE);
                emailStr=email.getText().toString().trim();
                passStr=pass.getText().toString().trim();
                repassStr=repass.getText().toString().trim();
                if(emailStr.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|in)$")){
                    if(!passStr.equals(repassStr))
                    {
                        mismatch.setText("Password mismatch");
                        mismatch.setVisibility(View.VISIBLE);
                    }
                    else{
                        progressBar.setVisibility(View.VISIBLE);
                        create.setEnabled(false);
                        if (isInternetAvailable(getApplicationContext())){
                        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        create.setEnabled(true);
                                        Intent intent=new Intent(getApplicationContext(), login.class);
                                        startActivity(intent);
                                        finishAffinity();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        create.setEnabled(true);
                                        Toast.makeText(signup.this, "Account already exists" , Toast.LENGTH_SHORT).show();
                                    }
                                });}
                        else {
                            progressBar.setVisibility(View.GONE);
                            create.setEnabled(true);
                            Toast.makeText(signup.this, "check internet connection", Toast.LENGTH_SHORT).show();

                        }


                    }
                }
                else {
                    mismatch.setText("Invalid email");
                    mismatch.setVisibility(View.VISIBLE);
                }

            }
        });
        //on click login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),com.android.weathercheck.login.class);
                startActivity(intent);
                finishAffinity();
            }
        });



    }
     private boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
