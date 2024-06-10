package com.example.uasproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uasproject.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.midtrans.Midtrans;
import com.midtrans.httpclient.CoreApi;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
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
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private UUID idRand;
    private Map<String, Object> chargeParams;
    private JSONObject result;
    private String course_id;
    private String generateQrCode, deeplinkRedirect, vaNumber, billerCode, billKey;
    private String permata_va_number, paymentCode, merchantId;
    private String selectedPaymentMethod;
    private ProgressBar progressBar;
    private Button btnBayar;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ImageView back = findViewById(R.id.back);

        String titleCourse = getIntent().getStringExtra("title_course");
        String agencyCourse = getIntent().getStringExtra("agency_course");
        priceCourse = getIntent().getStringExtra("price_course");
        course_id = getIntent().getStringExtra("course_id");

        priceForMidtrans = getIntent().getStringExtra("price_for_midtrans");

        TextView title = findViewById(R.id.title_course);
        TextView agency = findViewById(R.id.agency_course);
        TextView price = findViewById(R.id.price_course);
        TextView total_tagihan = findViewById(R.id.value_total_tagihan);
        TextView biaya_layanan = findViewById(R.id.value_biaya_layanan);
        btnBayar = findViewById(R.id.btn_bayar);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user_id = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("order");

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
                selectedPaymentMethod = parent.getItemAtPosition(position).toString();
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
            btnBayar.setBackgroundResource(R.drawable.button_shape_off);
            btnBayar.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

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
                            String order_id = result.getString("order_id");
                            String transaction_status = result.getString("transaction_status");
                            String order_date = result.getString("transaction_time");
                            String expiry_time = result.getString("expiry_time");

                            switch(type){
                                case "gopay":
                                    try{
                                        JSONArray actions = result.getJSONArray("actions");
                                        for(int i = 0; i<actions.length(); i++){
                                            JSONObject action = actions.getJSONObject(i);
                                            String name = action.getString("name");
                                            if(name.equals("generate-qr-code")){
                                                String url = action.getString("url");
                                                generateQrCode = url;
                                            }else if(name.equals("deeplink-redirect")){
                                                String url = action.getString("url");
                                                deeplinkRedirect = url;
                                            }
                                        }

                                        Map<String, Object> orderData = new HashMap<>();
                                        orderData.put("order_id", order_id);
                                        orderData.put("course_id", course_id);
                                        orderData.put("user_id", user_id);
                                        orderData.put("price", priceForMidtrans);
                                        orderData.put("payment_method", selectedPaymentMethod);
                                        orderData.put("transaction_status", transaction_status);
                                        orderData.put("order_date", order_date);
                                        orderData.put("expiry_time", expiry_time);
                                        orderData.put("url_qris", generateQrCode);
                                        orderData.put("deeplink_url", deeplinkRedirect);

                                        mDatabase.child(order_id).setValue(orderData);

                                        Intent intent = new Intent(this, ReferensiPembayaranQrisActivity.class);
                                        intent.putExtra("url_qris", generateQrCode);
                                        intent.putExtra("url_deeplink", deeplinkRedirect);
                                        intent.putExtra("order_id", order_id);
                                        intent.putExtra("payment_method", selectedPaymentMethod);
                                        intent.putExtra("total", priceForMidtrans);
                                        startActivity(intent);
                                        Log.d("Gopay", type);
                                        finish();

                                        btnBayar.setBackgroundResource(R.drawable.button_shape);
                                        btnBayar.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar.setIndeterminate(false);
                                    }catch(Exception e){
                                        btnBayar.setBackgroundResource(R.drawable.button_shape);
                                        btnBayar.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar.setIndeterminate(false);
                                        Toast.makeText(this, "Oops... Something Error", Toast.LENGTH_SHORT).show();
                                        Log.e("Gopay", String.valueOf(e));
                                    }
                                case "bank_transfer":
                                    try{
                                        merchantId = result.optString("merchant_id", null);
                                        permata_va_number = result.optString("permata_va_number", null);
                                        JSONArray vaNumbers = result.getJSONArray("va_numbers");
                                        for(int i=0; i<vaNumbers.length(); i++){
                                            JSONObject index = vaNumbers.getJSONObject(i);
                                            vaNumber = index.getString("va_number");
                                        }

                                        String vaNumberValue = (vaNumber != null) ? vaNumber : permata_va_number;

                                        Map<String, Object> orderData = new HashMap<>();
                                        orderData.put("order_id", order_id);
                                        orderData.put("course_id", course_id);
                                        orderData.put("user_id", user_id);
                                        orderData.put("price", priceForMidtrans);
                                        orderData.put("payment_method", selectedPaymentMethod);
                                        orderData.put("va_number", vaNumberValue);
                                        orderData.put("merchant_id", merchantId);
                                        orderData.put("transaction_status", transaction_status);
                                        orderData.put("order_date", order_date);
                                        orderData.put("expiry_time", expiry_time);

                                        mDatabase.child(order_id).setValue(orderData);

                                        Intent intent = new Intent(this, ReferensiPembayaranActivity.class);
                                        intent.putExtra("va_number", vaNumberValue);
                                        intent.putExtra("merchant_id", merchantId);
                                        intent.putExtra("order_id", order_id);
                                        intent.putExtra("payment_method", selectedPaymentMethod);
                                        intent.putExtra("total", priceForMidtrans);
                                        startActivity(intent);
                                        Log.d("BankTransfer", type);
                                        finish();

                                        btnBayar.setBackgroundResource(R.drawable.button_shape);
                                        btnBayar.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar.setIndeterminate(false);
                                    }catch(Exception e){
                                        btnBayar.setBackgroundResource(R.drawable.button_shape);
                                        btnBayar.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        progressBar.setIndeterminate(false);
                                        Toast.makeText(this, "Oops... Something Error", Toast.LENGTH_SHORT).show();
                                        Log.e("BankTransfer", String.valueOf(e));
                                    }
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
                        btnBayar.setBackgroundResource(R.drawable.button_shape);
                        btnBayar.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        progressBar.setIndeterminate(false);
                        Log.e("MidtransTest", String.valueOf(e));

                        runOnUiThread(() -> {
                            // Handle error response
                            Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                    Log.d("MidtransCharge", "Success");
                });
            }else{
                Toast.makeText(this, "Pilih salah satu metode pembayaran !", Toast.LENGTH_SHORT).show();
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