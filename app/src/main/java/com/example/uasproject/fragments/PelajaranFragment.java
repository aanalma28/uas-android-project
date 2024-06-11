package com.example.uasproject.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;
import com.example.uasproject.adapters.MyCourseAdapter;
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

public class PelajaranFragment extends Fragment implements RecycleViewInterface {

    private List<Map<String, Object>> courseList;
    private RecyclerView recyclerView;
    private MyCourseAdapter myCourseAdapter;
    private FirebaseAuth mAuth;
    private Context context;

    public PelajaranFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pelajaran, container, false);

        courseList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.pelajaran_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        myCourseAdapter = new MyCourseAdapter(this, courseList, context);
        recyclerView.setAdapter(myCourseAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user_id = user.getUid().toString();
        DBFirebase db = new DBFirebase();
        Query data = db.getSpecifyOrderUser(user_id);

        data.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("courseList", "DataSnapshot count: " + snapshot.getChildrenCount());
                courseList.clear();
                try{
                    if(snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Log.d("courseList", "DataSnapshot: " + dataSnapshot.toString());
                            Map<String, Object> order = (Map<String, Object>) dataSnapshot.getValue();
                            if(order != null && user_id.equals(order.get("user_id"))){
                                if (Objects.equals(order.get("transaction_status"), "settlement")) {
                                    courseList.add(order);
                                }
                            }
                        }
                        myCourseAdapter.notifyDataSetChanged();
                    }else{
                        Log.d("FirebaseData", "No courses found for this user.");
                    }
                }catch(Exception e){
                    Log.e("Get list Error", String.valueOf(e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("courseList", "Error fetching data: ", error.toException());
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {

    }
}