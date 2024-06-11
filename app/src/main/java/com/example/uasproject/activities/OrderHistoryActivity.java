package com.example.uasproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;
import com.example.uasproject.adapters.TransactionAdapter;
import com.example.uasproject.models.Course;
import com.example.uasproject.utils.DBFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrderHistoryActivity extends AppCompatActivity implements RecycleViewInterface{
    private RecyclerView orderHistoryRecycleView;
    private TransactionAdapter transactionAdapter;
    List<Map<String, Object>> orderList;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ImageView back = findViewById(R.id.back);

        orderList = new ArrayList<>();
        orderHistoryRecycleView = findViewById(R.id.order_history_recycle_view);
        orderHistoryRecycleView.setLayoutManager(new LinearLayoutManager(this));

        transactionAdapter = new TransactionAdapter( (RecycleViewInterface) this, orderList);
        orderHistoryRecycleView.setAdapter(transactionAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user_id = user.getUid().toString();
        DBFirebase db = new DBFirebase();
        Query data = db.getSpecifyOrderUser(user_id);

        data.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("OrderList", "DataSnapshot count: " + snapshot.getChildrenCount());
                orderList.clear();
                try{
                    if(snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Log.d("OrderList", "DataSnapshot: " + dataSnapshot.toString());
                            Map<String, Object> order = (Map<String, Object>) dataSnapshot.getValue();
                            Log.d("OrderList", order.toString());
                            if(order != null && user_id.equals(order.get("user_id"))){
                                orderList.add(order);
                            }
                        }
                        transactionAdapter.notifyDataSetChanged();
                    }else{
                        Log.d("FirebaseData", "No courses found for this user.");
                    }
                }catch(Exception e){
                    Log.e("Get list Error", String.valueOf(e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderList", "Error fetching data: ", error.toException());
            }
        });

        back.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onItemClick(int position) {
        String paymentType = (String) orderList.get(position).get("payment_type");
        Log.d("ItemClick", orderList.get(position).toString());
        switch(Objects.requireNonNull(paymentType)){
            case "gopay":
                try{
                    Log.d("Gopay", "Intent Gopay");
                    Intent intentGopay = new Intent(this, ReferensiPembayaranQrisActivity.class);
                    intentGopay.putExtra("url_qris", (String) orderList.get(position).get("url_qris"));
                    intentGopay.putExtra("url_deeplink", (String) orderList.get(position).get("url_deeplink"));
                    intentGopay.putExtra("order_id", (String) orderList.get(position).get("order_id"));
                    intentGopay.putExtra("order_date", (String) orderList.get(position).get("order_date"));
                    intentGopay.putExtra("expiry_time", (String) orderList.get(position).get("expiry_time"));
                    intentGopay.putExtra("payment_method", (String) orderList.get(position).get("payment_method"));
                    intentGopay.putExtra("total", (String) orderList.get(position).get("price"));
                    startActivity(intentGopay);
                    finish();
                }catch(Exception e){
                    Log.e("Gopay", String.valueOf(e));
                }
                break;
            case "bank_transfer":
                try{
                    Log.d("BankTransfer", "Intent BankTransfer");
                    Intent intentBank = new Intent(this, ReferensiPembayaranActivity.class);
                    intentBank.putExtra("va_number", (String) orderList.get(position).get("va_number"));
                    intentBank.putExtra("merchant_id", (String) orderList.get(position).get("merchant_id"));
                    intentBank.putExtra("order_id", (String) orderList.get(position).get("order_id"));
                    intentBank.putExtra("order_date", (String) orderList.get(position).get("order_date"));
                    intentBank.putExtra("expiry_time", (String) orderList.get(position).get("expiry_time"));
                    intentBank.putExtra("payment_method", (String) orderList.get(position).get("payment_method"));
                    intentBank.putExtra("total", (String) orderList.get(position).get("price"));
                    startActivity(intentBank);
                    finish();
                }catch(Exception e){
                    Log.e("BankTransfer", String.valueOf(e));
                }
                break;
            case "echannel":
                try{
                    Log.d("Echannel", "Intent Echannel");
                    Intent intentE = new Intent(this, ReferensiPembayaranEchannel.class);
                    intentE.putExtra("bill_key", (String) orderList.get(position).get("bill_key"));
                    intentE.putExtra("biller_code", (String) orderList.get(position).get("biller_code"));
                    intentE.putExtra("merchant_id", (String) orderList.get(position).get("merchant_id"));
                    intentE.putExtra("order_id", (String) orderList.get(position).get("order_id"));
                    intentE.putExtra("order_date", (String) orderList.get(position).get("order_date"));
                    intentE.putExtra("expiry_time", (String) orderList.get(position).get("expiry_time"));
                    intentE.putExtra("payment_method", (String) orderList.get(position).get("payment_method"));
                    intentE.putExtra("total", (String) orderList.get(position).get("price"));
                    startActivity(intentE);
                    finish();
                }catch(Exception e){
                    Log.e("Echannel", String.valueOf(e));
                }
                break;
            case "cstore":
                try{
                    Log.d("Cstore", "Intent Cstore");
                    Intent intentC = new Intent(this, ReferensiPembayaranCstore.class);
                    intentC.putExtra("payment_code", (String) orderList.get(position).get("payment_code"));
                    intentC.putExtra("merchant_id", (String) orderList.get(position).get("merchant_id"));
                    intentC.putExtra("order_id", (String) orderList.get(position).get("order_id"));
                    intentC.putExtra("order_date", (String) orderList.get(position).get("order_date"));
                    intentC.putExtra("expiry_time", (String) orderList.get(position).get("expiry_time"));
                    intentC.putExtra("payment_method", (String) orderList.get(position).get("payment_method"));
                    intentC.putExtra("total", (String) orderList.get(position).get("price"));
                    startActivity(intentC);
                    finish();
                }catch(Exception e){
                    Log.e("Cstore" , String.valueOf(e));
                }
                break;
        }
    }
}