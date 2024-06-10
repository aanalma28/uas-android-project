package com.example.uasproject.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uasproject.R;

public class ReferensiPembayaranActivity extends AppCompatActivity {
    private String va_number, merchant_id, order_id, payment_method, total;
    private TextView orderId, txtMerchant, merchantId, txtTotal, txtMethod;
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

        va_number = getIntent().getStringExtra("va_number");
        merchant_id = getIntent().getStringExtra("merchant_id");
        order_id = getIntent().getStringExtra("order_id");
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
    }
}