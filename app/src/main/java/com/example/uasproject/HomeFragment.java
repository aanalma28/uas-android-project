package com.example.uasproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Course> courseList;
    private RecyclerView homeRecycleView;
    private CourseAdapter courseAdapter;
    private DatabaseReference database;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

//        courseList = generateCourseItem();
        courseList = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference().child("course");

        homeRecycleView = rootView.findViewById(R.id.home_recycle_view);
        homeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        courseAdapter = new CourseAdapter(courseList);
        homeRecycleView.setAdapter(courseAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MainActivity", "DataSnapshot count: " + snapshot.getChildrenCount());
                courseList.clear();
                try{
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Log.d("MainActivity", "DataSnapshot: " + dataSnapshot.toString());
                        Course course = dataSnapshot.getValue(Course.class);
                        courseList.add(course);
                    }
                    courseAdapter.notifyDataSetChanged();
                }catch(Exception e){
                    Log.e("Get list Error", String.valueOf(e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors (e.g., display error message)
                Log.e("HomeFragment", "Error fetching data: ", error.toException());
            }
        });

        return rootView;
    }

//    private List<Course> generateCourseItem(){
//        List<Course> courses = new ArrayList<>();
//        courses.add(new Course("Persiapan UTBK", "200000"));
//        courses.add(new Course("Matematika", "200000"));
//        courses.add(new Course("Sejarah", "200000"));
//
//        return courses;
//    }
}