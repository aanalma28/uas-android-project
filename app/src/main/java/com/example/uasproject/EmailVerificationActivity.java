package com.example.uasproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EmailVerificationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btn;
    private TextView countdown, email;
    private EditText edtCode;
    private String code;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_email_verification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseUser user = mAuth.getCurrentUser();

        String bimbel = getIntent().getStringExtra(Constant.BIMBELNAME);
        String address = getIntent().getStringExtra(Constant.ADDRESS);
        String hp = getIntent().getStringExtra(Constant.PHONE);
        btn = findViewById(R.id.btn_verification);
        email = findViewById(R.id.txt_desc);
        countdown = findViewById(R.id.countdown_text);
        edtCode = findViewById(R.id.edt_code);

        email.setText("Buka Email yang kami kirimkan ke " + user.getEmail());

//        Not Use Firebase
        String subject = "Email Verification Code";
        Random random = new Random(System.currentTimeMillis());
        String mail = user.getEmail();

        btn.setOnClickListener(v -> {
            String inputCode = edtCode.getText().toString().trim();

            if(inputCode.length() != 4){
                Toast.makeText(this, "Code must have 4 digits", Toast.LENGTH_SHORT).show();
            }else{
                if(inputCode.equals(code)){
                    Log.d("Match", "Match Code: " + "Text = " + edtCode.getText().toString()
                            + "Rand: " + code);
                    Intent intent = new Intent(EmailVerificationActivity.this, RegisterSellerActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Kode tidak valid", Toast.LENGTH_SHORT).show();
                    Log.e("Not Match", "Not Match Code: " + "Text = " + edtCode.getText().toString()
                            + "Rand: " + code);
                }
            }
        });

        countdown.setOnClickListener(v -> {
            SendEmail(mail, subject);
        });


    }

    private int generateRandomNumber() {
        // Menghasilkan angka acak antara 1000 dan 9999
        return ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    private void SendEmail(String email, String subject){
        String random = String.valueOf(generateRandomNumber());
        code = random;
        String body = "Your Verification Code : " + random;
        try{
            EmailSender sendEmail = new EmailSender(email, subject, body);
            sendEmail.execute();

            Toast.makeText(this, "Email Sent.", Toast.LENGTH_SHORT).show();

            new CountDownTimer(6000, 1000){
                @SuppressLint("ResourceAsColor")
                public void onTick(long millisUntilFinished){
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    int minutes = (int) (millisUntilFinished / 1000) / 60;
                    String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                    countdown.setText(timeFormatted);
                    countdown.setClickable(false);
                    countdown.setTextColor(R.color.secondgrey);
                }

                @SuppressLint("ResourceAsColor")
                public void onFinish(){
                    countdown.setText("kirim ulang");
                    countdown.setTextColor(R.color.bluePrimary);
                    countdown.setClickable(true);
                }
            }.start();

        }catch(Exception e){
            Log.e("Send", String.valueOf(e));
        }
    }


}