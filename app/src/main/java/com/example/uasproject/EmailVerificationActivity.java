package com.example.uasproject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

public class EmailVerificationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email;
    private Button btn;
    private TextView countdown;
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

        String bimbel = getIntent().getStringExtra(Constant.BIMBELNAME);
        String address = getIntent().getStringExtra(Constant.ADDRESS);
        String hp = getIntent().getStringExtra(Constant.PHONE);
        btn = findViewById(R.id.btn_verification);
        countdown = findViewById(R.id.countdown_text);


        btn.setOnClickListener(v -> {
            try{
                btn.setEnabled(false);

                FirebaseUser user = mAuth.getCurrentUser();
                String id = user.getUid().toString();

                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.d("Sent Success", "Email Sent.");
                                }else{
                                    Log.e("Sent Error", task.getResult().toString());
                                }
                            }
                        });

                new CountDownTimer(6000, 1000){
                    public void onTick(long millisUntilFinished){
                        int seconds = (int) (millisUntilFinished / 1000) % 60;
                        int minutes = (int) (millisUntilFinished / 1000) / 60;
                        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                        countdown.setText(timeFormatted);
                    }

                    public void onFinish(){
                        countdown.setText("00:00");
                        btn.setBackgroundResource(R.drawable.button_shape);
                        btn.setEnabled(true);
                    }
                };

            }catch(Exception e){
                Log.e("Send Verification Error", String.valueOf(e));
            }
        });
    }
}