package com.example.uasproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uasproject.utils.DBFirebase;
import com.example.uasproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class RegisterSellerActivity extends AppCompatActivity {
    private EditText nama_bimbel, alamat, no_hp;
    private Button btn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public static final String SHARED_PREF_NAME = "myPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_seller);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nama_bimbel = findViewById(R.id.edt_nama_bimbel);
        alamat = findViewById(R.id.edt_alamat);
        no_hp = findViewById(R.id.edt_nohp);
        mAuth = FirebaseAuth.getInstance();

        btn = findViewById(R.id.btn_lanjut);

        btn.setOnClickListener(v -> {
            String bimbel = nama_bimbel.getText().toString();
            String almt = alamat.getText().toString();
            String hp = no_hp.getText().toString();

            if(bimbel.isEmpty() || almt.isEmpty() || hp.isEmpty()){
                Toast.makeText(this, "All field required", Toast.LENGTH_SHORT).show();
            }else{
//                Do Register
                try{
                    DBFirebase db = new DBFirebase();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String id = user.getUid().toString();
                    db.updateUserSeller(id, bimbel, hp, almt);

                    Log.d("ID USER", id);
                    Toast.makeText(this, "Successfully Register", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("nama", bimbel);
                    editor.putString("email", user.getEmail().toString());
                    editor.putString("role", "Agency");
                    editor.apply();

                    Intent intent = new Intent(RegisterSellerActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch(Exception e){
                    Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show();
                    Log.e("Register Fail", String.valueOf(e));
                }
            }
        });
    }
}