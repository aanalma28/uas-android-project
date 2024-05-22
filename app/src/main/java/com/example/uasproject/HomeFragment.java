package com.example.uasproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RecycleViewInterface {

    private List<Course> courseList;
    private RecyclerView homeRecycleView;
    private CourseAdapter courseAdapter;
    private DatabaseReference database;

    private SearchView searchView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        searchView = rootView.findViewById(R.id.search_box);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
//        courseList = generateCourseItem();
        courseList = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference().child("course");

        homeRecycleView = rootView.findViewById(R.id.home_recycle_view);
        homeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        courseAdapter = new CourseAdapter(courseList, this);
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

    private void filterList(String text) {
        Query searchQuery = database.orderByChild("name").startAt(text).endAt(text + "\uf8ff");
        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Course> courseLists = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Course course = dataSnapshot.getValue(Course.class);
                    if(course != null){
                        courseLists.add(course);
                    }
                }
                courseAdapter.updateList(courseLists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeActivity", "onCancelled", error.toException());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), DetailCourseActivity.class);

        intent.putExtra("title_course", courseList.get(position).getName());
        intent.putExtra("agency", courseList.get(position).getUser_id());
        intent.putExtra("desc", courseList.get(position).getDescription());
        intent.putExtra("price", String.valueOf(courseList.get(position).getPrice()));
        intent.putExtra("instructor", courseList.get(position).getInstructor());
        startActivity(intent);
        ((Activity) getActivity()).overridePendingTransition(0, 0);
    }
}