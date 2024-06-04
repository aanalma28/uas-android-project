package com.example.uasproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

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

    }
}