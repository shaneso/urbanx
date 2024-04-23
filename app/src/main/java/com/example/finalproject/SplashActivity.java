package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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