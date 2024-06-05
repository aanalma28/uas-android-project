package com.example.uasproject.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uasproject.R;

public class ReferensiPembayaranActivity extends AppCompatActivity {

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
    }
}