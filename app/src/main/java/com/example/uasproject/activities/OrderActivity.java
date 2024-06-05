package com.example.uasproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderActivity extends AppCompatActivity {
    private String paymentMethod;
    private String priceCourse, formattedAmount, priceForMidtrans;
    private String paymentType;
    private Boolean isChoosen = false;
    private UUID idRand;
    private Map<String, Object> chargeParams;
    private JSONObject result;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ImageView back = findViewById(R.id.back);

        String titleCourse = getIntent().getStringExtra("title_course");
        String agencyCourse = getIntent().getStringExtra("agency_course");
        priceCourse = getIntent().getStringExtra("price_course");

        priceForMidtrans = getIntent().getStringExtra("price_for_midtrans");

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

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Gopay");
        arrayList.add("Qris");
        arrayList.add("Alfamart");
        arrayList.add("Indomaret");
        arrayList.add("BCA Virtual Account");
        arrayList.add("BNI Virtual Account");
        arrayList.add("BRI Virtual Account");
        arrayList.add("Mandiri Virtual Account");
        arrayList.add("Permata Virtual Account");
        arrayList.add("CIMB Virtual Account");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    Get Selected Item
                String selectedPaymentMethod = parent.getItemAtPosition(position).toString();
                isChoosen = true;
                switch(selectedPaymentMethod){
                    case "Gopay":
                    case "Qris":
                        chargeParams = chargeParamsGopay(priceForMidtrans);
                        break;
                    case "Indomaret":
                        chargeParams = chargeparamsCstore("indomaret", priceForMidtrans);
                        break;
                    case "Alfamart":
                        chargeParams = chargeparamsCstore("alfamart", priceForMidtrans);
                        break;
                    case "BCA Virtual Account":
                        chargeParams = chargeParamsBankTransfer("bca", priceForMidtrans);
                        break;
                    case "BNI Virtual Account":
                        chargeParams = chargeParamsBankTransfer("bni", priceForMidtrans);
                        break;
                    case "BRI Virtual Account":
                        chargeParams = chargeParamsBankTransfer("bri", priceForMidtrans);
                        break;
                    case "Mandiri Virtual Account":
                        chargeParams = chargeParamsEchannel(priceForMidtrans);
                        break;
                    case "Permata Virtual Account":
                        chargeParams = chargeParamsPermata(priceForMidtrans);
                        break;
                    case "CIMB Virtual Account":
                        chargeParams = chargeParamsBankTransfer("cimb", priceForMidtrans);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(OrderActivity.this, "Pilih salah satu metode pembayaran !", Toast.LENGTH_SHORT).show();
                isChoosen = false;
            }
        });

        btnBayar.setOnClickListener(v -> {

            if(isChoosen){
                Midtrans.serverKey = "SB-Mid-server-qf4JZeBWNd9EM-a4K0R22sdV";
                Midtrans.isProduction = false;
                Midtrans.clientKey = "SB-Mid-client-mkDf5U-jwFf9Fogu";

                // Creating an ExecutorService
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(() -> {
                    try {
                        result = CoreApi.chargeTransaction(chargeParams);
                        Log.d("MidtransTest", String.valueOf(result));

                        if(result != null){
                            String type = result.getString("payment_type");

                            switch(type){
                                case "gopay":
                                    Log.d("MidtransType", type);
                                case "bank_transfer":
                                    Log.d("MidtransType", type);
                                case "echannel":
                                    Log.d("MidtransType", type);
                                case "cstore":
                                    Log.d("MidtransType", type);
                            }
                        }

                        runOnUiThread(() -> {
                            // Handle success response
                            Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show();
                        });
                    } catch (MidtransError |JSONException e) {
                        Log.e("MidtransTest", String.valueOf(e));

                        runOnUiThread(() -> {
                            // Handle error response
                            Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                    Log.d("MidtransCharge", "Success");
                });
            }
        });

    }

    private Map<String, Object> chargeParamsGopay(String grossAmount){
        idRand = UUID.randomUUID();
        Map<String, Object> chargeParams = new HashMap<>();

        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", idRand.toString());
        transactionDetails.put("gross_amount", grossAmount);

        chargeParams.put("transaction_details", transactionDetails);
        chargeParams.put("payment_type", "gopay");

        return chargeParams;
    }

    private Map<String, Object> chargeParamsBankTransfer(String method, String grossAmount){
        idRand = UUID.randomUUID();
        Map<String, Object> chargeParams = new HashMap<>();

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", idRand.toString());
        transactionDetails.put("gross_amount", grossAmount);

        Map<String, Object> bankTransfer = new HashMap<>();
        bankTransfer.put("bank", method);

        chargeParams.put("payment_type", "bank_transfer");
        chargeParams.put("transaction_details", transactionDetails);
        chargeParams.put("bank_transfer", bankTransfer);

        return chargeParams;
    }

    private Map<String, Object> chargeparamsCstore(String store, String grossAmount){
        idRand = UUID.randomUUID();
        Map<String, Object> chargeParams = new HashMap<>();

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", idRand.toString());
        transactionDetails.put("gross_amount", grossAmount);

        Map<String,Object> cStore = new HashMap<>();
        cStore.put("store", store);
        cStore.put("message", "Thank you for ordering with this Merchant !");

        chargeParams.put("payment_type", "cstore");
        chargeParams.put("transaction_details", transactionDetails);
        chargeParams.put("cstore", cStore);
        return chargeParams;
    }

    private Map<String, Object> chargeParamsEchannel(String grossAmount){
        idRand = UUID.randomUUID();

        Map<String, Object> chargeParams = new HashMap<>();

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", idRand.toString());
        transactionDetails.put("gross_amount", grossAmount);

        Map<String, Object> eChannel = new HashMap<>();
        eChannel.put("bill_info1", "Payment:");
        eChannel.put("bill_info2", "Online purchase");

        chargeParams.put("payment_type", "echannel");
        chargeParams.put("transaction_details", transactionDetails);
        chargeParams.put("echannel", eChannel);
        return chargeParams;
    }

    private Map<String, Object> chargeParamsPermata(String grossAmount){
        idRand = UUID.randomUUID();

        Map<String, Object> chargeParams = new HashMap<>();

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", idRand.toString());
        transactionDetails.put("gross_amount", grossAmount);

        chargeParams.put("payment_type", "permata");
        chargeParams.put("transaction_details", transactionDetails);
        return chargeParams;
    }
}