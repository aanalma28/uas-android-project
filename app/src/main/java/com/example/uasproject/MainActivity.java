package com.example.uasproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private PelajaranFragment pelajaranFragment = new PelajaranFragment();
    private DashboardFragment dashboardFragment = new DashboardFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private Button btn_logout;

    private DatabaseHelper db;

    public static final String SHARED_PREF_NAME = "myPref";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(this);

//        btn_logout = findViewById(R.id.btn_logout);
//
        db = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        Boolean checksession = db.checkSession("ada");
        if (checksession == false){
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }

//        btn_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Boolean updateSeesion = db.upgradeSession("kosong", 1);
//                if (updateSeesion == true){
//                    Toast.makeText(getApplicationContext(), "Berhasil Keluar", Toast.LENGTH_LONG).show();
//
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("masuk", false);
//                    editor.apply();
//
//                    Intent keluar = new Intent(MainActivity.this, LoginActivity.class);
//                    startActivity(keluar);
//                    finish();
//                }
//            }
//        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.home){
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, homeFragment).commit();
            return true;
        } else if (menuItem.getItemId() == R.id.pelajaran) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, pelajaranFragment).commit();
                return true;
        }else if (menuItem.getItemId() == R.id.dashboard) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, dashboardFragment).commit();
                return true;
        }else if (menuItem.getItemId() == R.id.profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment, profileFragment).commit();
                return true;
        }

        return false;
    }
}