package com.example.uasproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.uasproject.fragments.HomeFragment;
import com.example.uasproject.fragments.PelajaranFragment;
import com.example.uasproject.fragments.ProfileFragment;
import com.example.uasproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private PelajaranFragment pelajaranFragment = new PelajaranFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    public static final String SHARED_PREF_NAME = "myPref";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.home);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        Boolean masuk = sharedPreferences.getBoolean("masuk", true);
        if (masuk == false ){
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.home){
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, homeFragment).commit();
            return true;
        } else if (menuItem.getItemId() == R.id.pelajaran) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, pelajaranFragment).commit();
                return true;
        }else if (menuItem.getItemId() == R.id.profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, profileFragment).commit();
                return true;
        }

        return false;
    }
}