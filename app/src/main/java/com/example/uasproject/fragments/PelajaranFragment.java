package com.example.uasproject.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.uasproject.activities.DashboardActivity;
import com.example.uasproject.activities.DashboardDetailCourseActivity;
import com.example.uasproject.activities.DetailCourseActivity;
import com.example.uasproject.activities.DetailMyCourseActivity;
import com.example.uasproject.adapters.MyCourseAdapter;
import com.example.uasproject.models.Course;
import com.example.uasproject.models.User;
import com.example.uasproject.utils.DBFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PelajaranFragment extends Fragment implements RecycleViewInterface {

    private List<Course> courseList;
    private RecyclerView recyclerView;
    private MyCourseAdapter myCourseAdapter;
    private FirebaseAuth mAuth;
    Context context;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

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
                                    String course_id = (String) order.get("course_id");
                                    if (course_id != null) {
                                        mDatabase.child("course").child(course_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Course course = snapshot.getValue(Course.class);
                                                    if (course != null) {
                                                        courseList.add(course);
                                                        myCourseAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("GetCourseDetails", "Error fetching course data: ", error.toException());
                                            }
                                        });
                                    }
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
        Intent intent = new Intent(getActivity(), DetailMyCourseActivity.class);
        Log.d("OnClick", "onItemClick: " + courseList.get(position));
        Log.d("OnClick", "onItemClick: " + courseList.get(position).getUser_id());
        String id = (String) courseList.get(position).getUser_id();
        DBFirebase db = new DBFirebase();
        DatabaseReference data = db.getUser(id);

        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    String name = user.getName();
                    intent.putExtra("title_course", courseList.get(position).getName());
                    intent.putExtra("agency", name);
                    intent.putExtra("img", courseList.get(position).getImage());
                    intent.putExtra("desc", courseList.get(position).getDescription());
                    intent.putExtra("instructor", courseList.get(position).getInstructor());
                    intent.putExtra("course_id", courseList.get(position).getCourse_id());
                    startActivity(intent);
                    (requireActivity()).overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }
}