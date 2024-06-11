package com.example.uasproject.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.uasproject.R;
import com.example.uasproject.utils.CountDownViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReferensiPembayaranActivity extends AppCompatActivity {
    private String va_number, merchant_id, order_id, order_date, expiry_time, payment_method, total;
    private TextView orderId, txtMerchant, merchantId, txtTotal, txtMethod, status, txtExpired;
    private ImageView back;
    private String transactionStatus;
    private final int delay = 3000;
    private CountDownViewModel countDownViewModel;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("order");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referensi_pembayaran);
        TextView copy = findViewById(R.id.copy);
        TextView tvPaymentCode = findViewById(R.id.tvPaymentCode);
        copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Payment Code", tvPaymentCode.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ReferensiPembayaranActivity.this, "Kode pembayaran disalin", Toast.LENGTH_SHORT).show();
        });

        orderId = findViewById(R.id.order_id);
        txtMerchant = findViewById(R.id.txt_merchant);
        merchantId = findViewById(R.id.merchant_id);
        txtTotal = findViewById(R.id.total);
        txtMethod = findViewById(R.id.metode_pembayaran);
        txtExpired = findViewById(R.id.expired);
        status = findViewById(R.id.status);
        back = findViewById(R.id.back);

        va_number = getIntent().getStringExtra("va_number");
        merchant_id = getIntent().getStringExtra("merchant_id");
        order_id = getIntent().getStringExtra("order_id");
        order_date = getIntent().getStringExtra("order_date");
        expiry_time = getIntent().getStringExtra("expiry_time");
        payment_method = getIntent().getStringExtra("payment_method");
        total = getIntent().getStringExtra("total");

        if(merchant_id == null){
            txtMerchant.setVisibility(View.GONE);
            merchantId.setVisibility(View.GONE);
        }else{
            merchantId.setText(merchant_id);
        }

        tvPaymentCode.setText(va_number);
        orderId.setText(order_id);
        txtMethod.setText(payment_method);
        txtTotal.setText(total);

        back.setOnClickListener(v -> {
            finish();
        });

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
                                    mDatabase.child(order_id).child("transaction_status").setValue("settlement");

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