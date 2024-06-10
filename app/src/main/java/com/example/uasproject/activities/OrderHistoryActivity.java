package com.example.uasproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

    }
}