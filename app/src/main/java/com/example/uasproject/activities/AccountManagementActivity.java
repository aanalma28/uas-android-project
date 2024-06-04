package com.example.uasproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.uasproject.fragments.ProfileFragment;
import com.example.uasproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class AccountManagementActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        mAuth = FirebaseAuth.getInstance();

//        handle parameter from intent
        handleIncomingIntent(getIntent());
    }

    private void handleIncomingIntent(Intent intent){
        Uri data = intent.getData();
        if(data != null){
            String mode = data.getQueryParameter("mode");
            String obbCode = data.getQueryParameter("obbCode");

            if(mode != null && obbCode != null){
                switch(mode){
                    case "resetPassword":
                        handleResetPassword(obbCode);
                        break;
                    case "verifyEmail":
                        handleVerifyEmail(obbCode);
                        break;
                    case "recoverEmail":
                        handleRecoverEmail(obbCode);
                        break;
                    default:
                        Toast.makeText(this, "Unknown mode: " + mode, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void handleResetPassword(String obbCode){
//        handle reset password
    }

    private void handleVerifyEmail(String obbCode){
//        handle email verifiation
        mAuth.applyActionCode(obbCode)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(AccountManagementActivity.this, ProfileFragment.class);
                            Toast.makeText(AccountManagementActivity.this, "Email verified successfully", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void handleRecoverEmail(String obbCode){
//        handle recovery email
    }
}