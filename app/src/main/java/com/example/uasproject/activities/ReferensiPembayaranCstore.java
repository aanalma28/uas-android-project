package com.example.uasproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uasproject.R;
import com.example.uasproject.utils.CountDownViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReferensiPembayaranCstore extends AppCompatActivity {
    private String payment_code, merchant_id, order_id, order_date, expiry_time, payment_method, total;
    private TextView orderId, paymentCode, txtExpired, status, metodePembayaran;
    private TextView merchantId, txtTotal;
    private ImageView back;
    private String transactionStatus;
    private final int delay = 3000;
    private CountDownViewModel countDownViewModel;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("order");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referensi_pembayaran_cstore);

        payment_code = getIntent().getStringExtra("payment_code");
        merchant_id = getIntent().getStringExtra("merchant_id");
        order_id = getIntent().getStringExtra("order_id");
        expiry_time = getIntent().getStringExtra("expiry_time");
        payment_method = getIntent().getStringExtra("payment_method");
        total = getIntent().getStringExtra("total");

        orderId = findViewById(R.id.order_id);
        paymentCode = findViewById(R.id.tvPaymentCode);
        txtExpired = findViewById(R.id.expired);
        status = findViewById(R.id.status);
        metodePembayaran = findViewById(R.id.metode_pembayaran);
        merchantId = findViewById(R.id.merchant_id);
        txtTotal = findViewById(R.id.total);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> {
            finish();
        });

        orderId.setText(order_id);
        paymentCode.setText(payment_code);
        metodePembayaran.setText(payment_method);
        merchantId.setText(merchant_id);

        int totalInt = Integer.parseInt(total);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
        formatter.applyPattern("Rp###,###");
        String formattedPrice = formatter.format(totalInt);
        txtTotal.setText(formattedPrice);

        handler.post(runnableCode);
    }

    private void countTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date expiryDate = dateFormat.parse(expiry_time);
            Date transactionDate = dateFormat.parse(order_date);

            if (expiryDate != null && transactionDate != null) {
                long timeDifferenceMills = expiryDate.getTime() - transactionDate.getTime();
                countDownViewModel = new ViewModelProvider(this).get(CountDownViewModel.class);
                countDownViewModel.startTimer(timeDifferenceMills);

                countDownViewModel.getTimeRemaining().observe(this, time -> {
                    runOnUiThread(() -> {
                        txtExpired.setText(time);
                    });
                });
            }
        } catch (Exception e) {
            Log.e("CountdownError", String.valueOf(e));
        }
    }

    private final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            makeApiRequest();
        }
    };

    @SuppressLint("ResourceAsColor")
    private void makeApiRequest(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.sandbox.midtrans.com/v2/"+order_id+"/status")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Basic U0ItTWlkLXNlcnZlci1xZjRKWmVCV05kOUVNLWE0SzBSMjJzZFY6")
                .build();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try{
                Response response = client.newCall(request).execute();
                Log.d("TransactionData", String.valueOf(response));
                if(response.isSuccessful()){
                    String responseBody = response.body().string();
                    JSONObject result = new JSONObject(responseBody);
                    Log.d("MidtransTest", result.toString());

                    transactionStatus = result.getString("transaction_status");

                    runOnUiThread(() -> {
                        try {
                            if ("expire".equalsIgnoreCase(transactionStatus) || "settlement".equalsIgnoreCase(transactionStatus)) {
                                if (transactionStatus.equals("settlement")) {
                                    Log.d("CheckStatus", transactionStatus);
                                    status.setText("Success");
                                    status.setTextColor(getResources().getColor(R.color.success));
                                    String isDone = txtExpired.getText().toString();

                                    if(!isDone.equals("00:00:00")){
                                        mDatabase.child(order_id).child("transaction_status").setValue("settlement");
                                        mDatabase.child(order_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String course_id = snapshot.child("course_id").getValue(String.class);
                                                DatabaseReference courseDb = FirebaseDatabase.getInstance().getReference("course");

                                                courseDb.child(course_id).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String user_id = snapshot.child("user_id").getValue(String.class);
                                                        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference("users");

                                                        usersDb.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                String wallet = snapshot.child("wallet").getValue(String.class);
                                                                int income = Integer.parseInt(total);

                                                                if(wallet != null){
                                                                    Map<String, Object> data = new HashMap<>();
                                                                    int currentWallet = Integer.parseInt(wallet);
                                                                    int incomeWallet = currentWallet+income;
                                                                    String strIncomeWallet = String.valueOf(incomeWallet);
                                                                    data.put("wallet", strIncomeWallet);

                                                                    usersDb.child(user_id).updateChildren(data);
                                                                }else{
                                                                    Map<String, Object> data = new HashMap<>();
                                                                    data.put("wallet", total);

                                                                    usersDb.child(user_id).updateChildren(data);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Log.e("UsersSellerData", String.valueOf(error));
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.e("CourseData", String.valueOf(error));
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("OrderData", String.valueOf(error));
                                            }
                                        });
                                    }

                                    if (countDownViewModel != null) {
                                        countDownViewModel.stopTimer();
                                    }

                                } else if (transactionStatus.equals("expire")) {
                                    Log.d("CheckStatus", transactionStatus);
                                    status.setText("Expired");
                                    status.setTextColor(getResources().getColor(R.color.alertred));
                                    mDatabase.child(order_id).child("transaction_status").setValue("expire");

                                    if (countDownViewModel != null) {
                                        countDownViewModel.stopTimer();
                                    }
                                }
                                handler.removeCallbacks(runnableCode);
                            } else if ("pending".equalsIgnoreCase(transactionStatus)) {
                                // Continue polling
                                countTime();
                                handler.postDelayed(runnableCode, delay);
                            } else {
                                handler.removeCallbacks(runnableCode);
                            }
                        } catch (Exception e) {
                            Log.e("UIUpdateError", "Error updating UI", e);
                        }
                    });
                }else{
                    Log.e("MidtransTest", "Unsuccessful response: " + response.message());
                    runOnUiThread(() -> {
                        // Retry on unsuccessful response
                        handler.postDelayed(runnableCode, delay);
                    });
                }
            }catch(IOException e){
                Log.e("MidtransTest", "Request failed", e);
                runOnUiThread(() -> {
                    // Retry on error
                    handler.postDelayed(runnableCode, delay);
                });
            }
            catch(Exception e){
                Log.e("MidtransTest", "Unexpected error", e);
                runOnUiThread(() -> {
                    // Retry on unexpected error
                    handler.postDelayed(runnableCode, delay);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
    }
}