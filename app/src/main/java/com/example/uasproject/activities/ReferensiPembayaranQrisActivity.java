package com.example.uasproject.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uasproject.R;

public class ReferensiPembayaranQrisActivity extends AppCompatActivity {
    private String order_id, url_deeplink, url_qris, total;
    private ImageView qrisImg;
    private TextView totalTxt, orderIdTxt;
    private Button lanjutkan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referensi_pembayaran_qris);

        order_id = getIntent().getStringExtra("order_id");
        url_deeplink = getIntent().getStringExtra("url_deeplink");
        url_qris = getIntent().getStringExtra("url_qris");
        total = getIntent().getStringExtra("total");

        lanjutkan = findViewById(R.id.lanjutkan);
        totalTxt = findViewById(R.id.total);
        orderIdTxt = findViewById(R.id.order_id);
        qrisImg = findViewById(R.id.qris);

        totalTxt.setText(total);
        orderIdTxt.setText(order_id);
        Glide.with(this).load(url_qris).fitCenter().into(qrisImg);

        lanjutkan.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_deeplink));
            startActivity(intent);
        });
    }
}