package com.example.uasproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DraftActivity extends AppCompatActivity implements RecycleViewInterface{
    private List<Course> courseList;
    private DatabaseReference mDatabase;
    private RecyclerView draftRecycleView;
    private DraftCourseAdapter draftCourseAdapter;
    private FirebaseAuth mAuth;
    public DraftActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            finish();
        });

        courseList = new ArrayList<>();

        draftRecycleView = findViewById(R.id.draft_recycle_view);
        draftRecycleView.setLayoutManager(new LinearLayoutManager(this));

        draftCourseAdapter = new DraftCourseAdapter(courseList, (RecycleViewInterface) this);
        draftRecycleView.setAdapter(draftCourseAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user_id = user.getUid().toString();
        DBFirebase db = new DBFirebase();
        Query data = db.getSpecifyCloseCourse();

        data.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DraftActivity", "DataSnapshot count: " + snapshot.getChildrenCount());
                courseList.clear();
                try{
                    if(snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Log.d("DraftActivity", "DataSnapshot: " + dataSnapshot.toString());
                            Course course = dataSnapshot.getValue(Course.class);
                            if(course != null && user_id.equals(course.getUser_id()) && course.getStatus().equals("close")){
                                courseList.add(course);
                            }
                        }
                        draftCourseAdapter.notifyDataSetChanged();
                    }else{
                        Log.d("FirebaseData", "No courses found for this user.");
                    }
                }catch(Exception e){
                    Log.e("Get list Error", String.valueOf(e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors (e.g., display error message)
                Log.e("DraftActivity", "Error fetching data: ", error.toException());
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }
}