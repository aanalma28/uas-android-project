package com.example.uasproject.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uasproject.R;

import com.midtrans.Midtrans;
import com.midtrans.httpclient.CoreApi;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ImageView back = findViewById(R.id.back);

        String titleCourse = getIntent().getStringExtra("title_course");
        String agencyCourse = getIntent().getStringExtra("agency_course");
        String priceCourse = getIntent().getStringExtra("price_course");

        TextView title = findViewById(R.id.title_course);
        TextView agency = findViewById(R.id.agency_course);
        TextView price = findViewById(R.id.price_course);
        TextView total_tagihan = findViewById(R.id.value_total_tagihan);
        TextView biaya_layanan = findViewById(R.id.value_biaya_layanan);
        Button btnBayar = findViewById(R.id.btn_bayar);

        assert priceCourse != null;
        String normalizedPrice = priceCourse.replace("Rp", "").replace(".", "");
        int priceCourseInt = Integer.parseInt(normalizedPrice);

        String getBiayaLayanan = biaya_layanan.getText().toString();
        String normalizedBiayaLayanan = getBiayaLayanan.replace("Rp", "").replace(".", "");
        int biayaLayananInt = Integer.parseInt(normalizedBiayaLayanan);

        int totalTagihanInt = priceCourseInt + biayaLayananInt;

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
        formatter.applyPattern("Rp###,###");
        String formattedTotalTagihan = formatter.format(totalTagihanInt);

        total_tagihan.setText(formattedTotalTagihan);

        title.setText(titleCourse);
        agency.setText(agencyCourse);
        price.setText(priceCourse);

        back.setOnClickListener(v -> {
            finish();
        });

        Spinner spinner = findViewById(R.id.spinner_method);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = String.valueOf(parent.getItemIdAtPosition(position));
                Toast.makeText(OrderActivity.this, "selected: " + item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Gopay");
        arrayList.add("Qris");
        arrayList.add("alfamart");
        arrayList.add("Indomart");
        arrayList.add("BCA Virtual Account");
        arrayList.add("BNI Virtual Account");
        arrayList.add("BRI Virtual Account");
        arrayList.add("Mandiri Virtual Account");
        arrayList.add("Permata Virtual Account");
        arrayList.add("CIMB Virtual Account");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);

        btnBayar.setOnClickListener(v -> {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
            Midtrans.serverKey = "SB-Mid-server-qf4JZeBWNd9EM-a4K0R22sdV";
            Midtrans.isProduction = false;
            Midtrans.clientKey = "SB-Mid-client-mkDf5U-jwFf9Fogu";

            UUID idRand = UUID.randomUUID();
            Map<String, Object> chargeParams = new HashMap<>();

            Map<String, String> transactionDetails = new HashMap<>();
            transactionDetails.put("order_id", idRand.toString());
            transactionDetails.put("gross_amount", "265000");

            chargeParams.put("transaction_details", transactionDetails);
            chargeParams.put("payment_type", "gopay");

            // Creating an ExecutorService
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                JSONObject result = null;
                try {
                    result = CoreApi.chargeTransaction(chargeParams);
                    Log.d("MidtransTest", String.valueOf(result));

                    runOnUiThread(() -> {
                        // Handle success response
                        Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show();
                    });
                } catch (MidtransError e) {
                    Log.e("MidtransTest", String.valueOf(e));

                    runOnUiThread(() -> {
                        // Handle error response
                        Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                    });
                }
                Log.d("MidtransCharge", "Success");
            });
        });

    }
}