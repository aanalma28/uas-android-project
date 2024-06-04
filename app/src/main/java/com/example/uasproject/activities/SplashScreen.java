package com.example.uasproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uasproject.R;
import com.google.firebase.auth.FirebaseAuth;

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
                boolean logged = sharedPreferences.getBoolean("masuk", false);

                try{
                    if (logged){
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }else {
                        startActivity(new Intent(SplashScreen.this, LandingActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }catch(Exception e){
                    Log.e("Splash Error", String.valueOf(e));
                }
            }
        }, 3000);
    }
}