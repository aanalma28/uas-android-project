package com.example.uasproject.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;
import com.example.uasproject.activities.DetailCourseActivity;
import com.example.uasproject.adapters.CourseAdapter;
import com.example.uasproject.models.Course;
import com.example.uasproject.models.Seller;
import com.example.uasproject.utils.DBFirebase;
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

        courseList = new ArrayList<>();

        searchView = rootView.findViewById(R.id.search_box);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        database = FirebaseDatabase.getInstance().getReference().child("course");

        homeRecycleView = rootView.findViewById(R.id.home_recycle_view);
        homeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        courseAdapter = new CourseAdapter(courseList, this);
        homeRecycleView.setAdapter(courseAdapter);

        Query data = database.child("status").equalTo("open");

        database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MainActivity", "DataSnapshot count: " + snapshot.getChildrenCount());
                courseList.clear();
//                Log.d("MainActivity", "DataSnapshot count: " + snapshot.toString());
                try{
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Log.d("MainActivity", "DataSnapshot: " + dataSnapshot.toString());
                        Course course = dataSnapshot.getValue(Course.class);
                        if(course != null && course.getStatus().equals("open")){
                            courseList.add(course);
                        }
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
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Course course = dataSnapshot.getValue(Course.class);
                    if(course != null && !course.getStatus().equals("close")){
                        courseList.add(course);
                    }
                }
                courseAdapter.setFilteredList(courseList);
                courseAdapter.notifyDataSetChanged();
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
        String id = courseList.get(position).getUser_id();
        DBFirebase db = new DBFirebase();
        DatabaseReference data = db.getUser(id);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("id", "ID"));
        formatter.applyPattern("Rp###,###");
        String formattedPrice = formatter.format(courseList.get(position).getPrice());

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Seller seller = snapshot.getValue(Seller.class);

                if(seller != null){
                    String name = seller.getAgency();
                    intent.putExtra("course_id", courseList.get(position).getCourse_id());
                    intent.putExtra("title_course", courseList.get(position).getName());
                    intent.putExtra("agency", name);
                    intent.putExtra("img", courseList.get(position).getImage());
                    intent.putExtra("desc", courseList.get(position).getDescription());
                    intent.putExtra("price", formattedPrice);
                    intent.putExtra("instructor", courseList.get(position).getInstructor());
                    startActivity(intent);
//                ((Activity) getActivity()).overridePendingTransition(0, 0);

                }else{
                    Log.e("User", "User is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }
}