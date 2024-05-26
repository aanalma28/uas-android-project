package com.example.uasproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class GantiPasswordActivity extends AppCompatActivity {
    private EditText oldPassword, newPassword, confirmPassword;
    private Button btnChangePass;
    private ProgressBar progressBar;
    private FirebaseUser userAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);
        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(v -> {
            finish();
        });

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        DBFirebase db = new DBFirebase();
        String id = userAuth.getUid();

        oldPassword = findViewById(R.id.edt_current_pass);
        newPassword = findViewById(R.id.edt_new_pass);
        confirmPassword = findViewById(R.id.edt_confirm_pass);
        btnChangePass = findViewById(R.id.btn_change_pass);
        progressBar = findViewById(R.id.progressBar);

        DatabaseReference data = db.getUser(id);

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userData = snapshot.getValue(User.class);
                String old_pass = userData.getPassword();

                oldPassword.setText(old_pass);
                oldPassword.setEnabled(false);

                btnChangePass.setOnClickListener(v -> {
                    btnChangePass.setBackgroundResource(R.drawable.button_shape_off);
                    btnChangePass.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

                    String new_pass = newPassword.getText().toString();
                    String conf_pass = confirmPassword.getText().toString();

                    if(!conf_pass.equals(new_pass)){
                        confirmPassword.setError("Password tidak sama");
                        btnChangePass.setBackgroundResource(R.drawable.button_shape);
                        btnChangePass.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        progressBar.setIndeterminate(false);
                    }else{
                        try{
                            Log.d("NewPass", new_pass);
                            userAuth.updatePassword(new_pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        db.updatePassword(id, new_pass);

                                        btnChangePass.setBackgroundResource(R.drawable.button_shape);
                                        btnChangePass.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar.setIndeterminate(false);
                                        Toast.makeText(GantiPasswordActivity.this, "Password berhasil diganti", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else{
                                        btnChangePass.setBackgroundResource(R.drawable.button_shape);
                                        btnChangePass.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar.setIndeterminate(false);
                                        Toast.makeText(GantiPasswordActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        Log.e("UserAuth", "Update password auth fail");
                                    }
                                }
                            });
                        }catch(Exception e){
                            btnChangePass.setBackgroundResource(R.drawable.button_shape);
                            btnChangePass.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            progressBar.setIndeterminate(false);
                            Toast.makeText(GantiPasswordActivity.this, "Oops... Something seems error. Try Again", Toast.LENGTH_SHORT).show();
                            Log.e("UserAuth", String.valueOf(e));
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", String.valueOf(error));
            }
        });

    }
}