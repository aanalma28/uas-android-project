package com.example.uasproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
    SharedPreferences sharedPreferences;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView nama_akun = view.findViewById(R.id.nama_akun);
        TextView email_akun = view.findViewById(R.id.email_akun);
        Button btn_logout = view.findViewById(R.id.logout);
        Button dashboard = view.findViewById(R.id.dashboard);
        Button ganti_pass = view.findViewById(R.id.ganti_pass);

        sharedPreferences = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");
        btn_logout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            startActivity(intent);
            getActivity().finish();

        });

        ganti_pass.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GantiPasswordActivity.class);
            startActivity(intent);
        });

        dashboard.setOnClickListener(v -> {
            if (role.equals("agency")) {
                Log.d("Role", "sharedpreferences: " + role);
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                startActivity(intent);
            }else {
                Log.d("Role", "sharedpreferences: " + role);
                Intent intent = new Intent(getActivity(), DashboardRegisterActivity.class);
                startActivity(intent);
            }
        });

        try{
            String nama = sharedPreferences.getString("nama", "");
            String email = sharedPreferences.getString("email", "");
            nama_akun.setText(nama);
            email_akun.setText(email);
//            if (role != "agency"){
//                dashboard.setText("Daftar bimbel");
//                Log.d("role", "onCreateView: "+role);
//            }

        }catch(Exception e){
            Log.e("Data error", String.valueOf(e));
        }

        return view;
    }

}