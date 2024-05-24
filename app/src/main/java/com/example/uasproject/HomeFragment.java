package com.example.uasproject;

import android.annotation.SuppressLint;
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


        courseList = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference().child("course");

        homeRecycleView = rootView.findViewById(R.id.home_recycle_view);
        homeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        courseAdapter = new CourseAdapter(courseList, this);
        homeRecycleView.setAdapter(courseAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("MainActivity", "DataSnapshot count: " + snapshot.getChildrenCount());
                courseList.clear();
                try{
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Log.d("MainActivity", "DataSnapshot: " + dataSnapshot.toString());
                        Course course = dataSnapshot.getValue(Course.class);
                        if(course != null && !course.getStatus().equals("close")){
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
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Course> courseLists = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Course course = dataSnapshot.getValue(Course.class);
                    if(course != null && !course.getStatus().equals("close")){
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
        String id = courseList.get(position).getUser_id();
        DBFirebase db = new DBFirebase();
        DatabaseReference data = db.getUser(id);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String formattedPrice = formatRupiah.format(courseList.get(position).getPrice());
        formattedPrice = formattedPrice.replace("Rp", "Rp. ").replace(",00", "");
        String finalFormattedPrice = formattedPrice;
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String name = user.getName();

                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                String formattedPrice = formatRupiah.format(courseList.get(position).getPrice());
                formattedPrice = formattedPrice.replace("Rp", "Rp. ").replace(",00", "");
                intent.putExtra("title_course", courseList.get(position).getName());
                intent.putExtra("agency", name);
                intent.putExtra("desc", courseList.get(position).getDescription());
<<<<<<< HEAD
                intent.putExtra("price", formattedPrice);
=======
                intent.putExtra("price", finalFormattedPrice);
>>>>>>> a59cbc6665deaec8b07d89cd57098696dd132999
                intent.putExtra("instructor", courseList.get(position).getInstructor());
                startActivity(intent);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }
}