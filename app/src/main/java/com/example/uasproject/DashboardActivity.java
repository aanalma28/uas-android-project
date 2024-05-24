package com.example.uasproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    TextView opsiInfoBimbel;
    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ImageView back = findViewById(R.id.back);
        ImageView options = findViewById(R.id.options);
        Button createCourse = findViewById(R.id.btn_tambah_pelajaran);
        Button draft = findViewById(R.id.btn_draft);
        opsiInfoBimbel = findViewById(R.id.info_bimbel);
        TextView nama_bimbel = findViewById(R.id.txt_agency);
        TextView info_bimbel = findViewById(R.id.info_bimbel);
        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        info_bimbel.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, InfoBimbelActivity.class);
            startActivity(intent);
        });

        back.setOnClickListener(v -> {
            finish();
        });
        createCourse.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CreateCourseActivity.class);
            startActivity(intent);
        });
        draft.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DraftActivity.class);
            startActivity(intent);
        });
        options.setOnClickListener(v -> {
           toggleInfoBimbel();
        });

        String agency = sharedPreferences.getString("agency", null);
        if (agency == null) {
            // Jika agency tidak ada, ambil dari Firebase Realtime Database
            getAgencyFromFirebaseAndSaveToSharedPreferences(sharedPreferences);
        } else {
            // Jika agency ada, gunakan nilai dari SharedPreferences
            Log.d(TAG, "Agency from SharedPreferences: " + agency);
            nama_bimbel.setText("Pelajaran di " + agency);
            // Anda dapat menggunakan nilai agency di sini
        }
    }
    private void toggleInfoBimbel() {
        Boolean cekvisibel = opsiInfoBimbel.getVisibility() == View.GONE;
        if (cekvisibel.equals(true)) {
            opsiInfoBimbel.setVisibility(View.VISIBLE);
        } else {
            opsiInfoBimbel.setVisibility(View.GONE);
        }
    }

    private void getAgencyFromFirebaseAndSaveToSharedPreferences(SharedPreferences sharedPreferences) {
        // Mendapatkan instance FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Mendapatkan pengguna saat ini
        FirebaseUser currentUser = auth.getCurrentUser();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (currentUser != null) {
            // Mendapatkan UID dari pengguna saat ini
            String uid = currentUser.getUid();

            // Mendapatkan reference ke Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // Mengakses node pengguna di Firebase Realtime Database menggunakan UID
            DatabaseReference userRef = databaseReference.child("users").child(uid);

            // Menambahkan listener untuk mengambil data pengguna
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Memastikan data pengguna ada
                    if (dataSnapshot.exists()) {
                        // Mengambil field agency
                        String agency = dataSnapshot.child("agency").getValue(String.class);
                        String address = dataSnapshot.child("address").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);
                        Log.d(TAG, "Agency: " + agency);
                        Log.d(TAG, "Address: " + address);
                        Log.d(TAG, "Phone: " + phone);
                        editor.putString("agency", agency);
                        editor.putString("address", address);
                        editor.putString("phone", phone);
                        editor.apply();
                        // Anda dapat menggunakan field agency di sini (misalnya menampilkannya di UI)
                    } else {
                        Log.d(TAG, "Data pengguna tidak ditemukan.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Database error: " + databaseError.getMessage());
                }
            });
        } else {
            Log.d(TAG, "Pengguna belum login.");
        }
    }
}