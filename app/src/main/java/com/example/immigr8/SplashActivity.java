package com.example.immigr8;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*
         * Change email to log in with different accounts
         */
        FirebaseAuth.getInstance().signInWithEmailAndPassword("mackenziedy@hotmail.com", "mackenzie")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }, 500);
                    } else {
                        Toast.makeText(this, "Sign In Failure", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}