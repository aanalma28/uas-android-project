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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        Intent intent = new Intent(DraftActivity.this, DashboardDetailCourseActivity.class);
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
                if (user != null) {
                    String name = user.getName();
                    intent.putExtra("title_course", courseList.get(position).getName());
                    intent.putExtra("agency", name);
                    intent.putExtra("img", courseList.get(position).getImage());
                    intent.putExtra("desc", courseList.get(position).getDescription());
                    intent.putExtra("price", finalFormattedPrice);
                    intent.putExtra("instructor", courseList.get(position).getInstructor());
                    intent.putExtra("course_id", courseList.get(position).getCourse_id());
                    startActivity(intent);
                    (DraftActivity.this).overridePendingTransition(0, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }
}