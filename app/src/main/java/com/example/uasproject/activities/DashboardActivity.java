package com.example.uasproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uasproject.utils.DBFirebase;
import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;
import com.example.uasproject.adapters.DashboardCourseAdapter;
import com.example.uasproject.models.Course;
import com.example.uasproject.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity implements RecycleViewInterface {
    private static final String TAG = "DashboardActivity";
    TextView opsiInfoBimbel, valueSaldo;
    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    private List<Course> courseList;
    private RecyclerView dashboardRecycleView;
    private DashboardCourseAdapter dashboardCourseAdapter;
    private DatabaseReference mDatabase;

    public DashboardActivity(){

    }

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
        valueSaldo = findViewById(R.id.value_saldo);

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

        courseList = new ArrayList<>();

        dashboardRecycleView = findViewById(R.id.dashboard_recycle_view);
        dashboardRecycleView.setLayoutManager(new LinearLayoutManager(this));

        dashboardCourseAdapter = new DashboardCourseAdapter( (RecycleViewInterface) this, courseList);
        dashboardRecycleView.setAdapter(dashboardCourseAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user_id = user.getUid().toString();
        DBFirebase db = new DBFirebase();
        Query data = db.getSpecifyOpenCourse();
        db.getUser(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String wallet = snapshot.child("wallet").getValue(String.class);
                if (wallet != null) {
                    try {
                        int walletInt = Integer.parseInt(wallet);

                        // Membuat instance NumberFormat untuk Indonesia
                        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

                        // Memformat angka ke dalam format mata uang
                        String formattedPrice = formatter.format(walletInt);

                        valueSaldo.setText(formattedPrice);
                    } catch (NumberFormatException e) {
                        Log.e("FormatError", "Invalid number format: " + e.getMessage());
                        valueSaldo.setText("Rp0");
                    }
                } else {
                    valueSaldo.setText("Rp0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GetUserError", String.valueOf(error));
            }
        });

        data.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "DataSnapshot count: " + snapshot.getChildrenCount());
                courseList.clear();
                try{
                    if(snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Log.d(TAG, "DataSnapshot: " + dataSnapshot.toString());
                            Course course = dataSnapshot.getValue(Course.class);
                            if(course != null && user_id.equals(course.getUser_id())){
                                courseList.add(course);
                            }
                        }
                        dashboardCourseAdapter.notifyDataSetChanged();
                    }else{
                        Log.d("FirebaseData", "No courses found for this user.");
                    }
                }catch(Exception e){
                    Log.e("Get list Error", String.valueOf(e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors (e.g., display error message)
                Log.e(TAG, "Error fetching data: ", error.toException());
            }
        });
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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(DashboardActivity.this, DashboardDetailCourseActivity.class);
        String id = courseList.get(position).getUser_id();
        DBFirebase db = new DBFirebase();
        DatabaseReference data = db.getUser(id);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
        formatter.applyPattern("Rp###,###");
        String formattedPrice = formatter.format(courseList.get(position).getPrice());

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    String name = user.getName();
                    intent.putExtra("title_course", courseList.get(position).getName());
                    intent.putExtra("agency", name);
                    intent.putExtra("img", courseList.get(position).getImage());
                    intent.putExtra("desc", courseList.get(position).getDescription());
                    intent.putExtra("price", formattedPrice);
                    intent.putExtra("instructor", courseList.get(position).getInstructor());
                    intent.putExtra("course_id", courseList.get(position).getCourse_id());
                    startActivity(intent);
                    (DashboardActivity.this).overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }
}