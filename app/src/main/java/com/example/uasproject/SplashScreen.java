package com.example.uasproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        View decorview = getWindow().getDecorView();
        decorview.setSystemUiVisibility(decorview.SYSTEM_UI_FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, 0);
//                boolean logged = sharedPreferences.getBoolean("masuk", false);

                try{
                    FirebaseUser getUser = mAuth.getCurrentUser();
                    if (getUser != null){
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(SplashScreen.this, LandingActivity.class));
                        finish();
                    }
                }catch(Exception e){
                    Log.e("Splash Error", String.valueOf(e));
                }
            }
        }, 3000);
    }
}