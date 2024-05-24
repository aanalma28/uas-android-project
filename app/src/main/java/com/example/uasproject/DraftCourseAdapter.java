package com.example.uasproject;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DraftCourseAdapter extends RecyclerView.Adapter<DraftCourseAdapter.CourseViewHolder> {
    private RecycleViewInterface recycleViewInterface;
    private List<Course> courseList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
    public DraftCourseAdapter(List<Course> courseList, RecycleViewInterface recycleViewInterface) {
        this.courseList = courseList;
        this.recycleViewInterface = recycleViewInterface;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(List<Course> filteredList){
        this.courseList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_draft, parent, false);
        return new CourseViewHolder(v, recycleViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        try{
            Course course = courseList.get(position);
            DBFirebase db = new DBFirebase();
            String user_id = mAuth.getCurrentUser().getUid();
            DatabaseReference user = db.getUser(user_id);

            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("Course Data", "DataSnapshot count: " + snapshot.getChildrenCount());
                    if(snapshot.exists()){
                        Seller seller = snapshot.getValue(Seller.class);
                        Log.d("Course Data", "DataSnapshot: " + course.toString());
                        Log.d("Course Data", "Seller: " + seller.getAgency());
                        holder.title.setText(course.getName());
                        holder.agency.setText(seller.getAgency());
                        holder.description.setText(course.getDescription());


                        Log.d("Course Data", "Course found");
                    }else{
                        Log.e("Course Data", "Course doesnt exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Course Data", "Failed to read value.", error.toException());
                }
            });
        }catch(Exception e){
            Log.e("Bind Error", String.valueOf(e));
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder{
        public TextView title, agency, description;
        public ImageView img_course;
        public CourseViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface){
            super(itemView);
            try {
                title = itemView.findViewById(R.id.title_course);
                agency = itemView.findViewById(R.id.name_bimbel);
                description = itemView.findViewById(R.id.desc_course);
                img_course = itemView.findViewById(R.id.img_course);

                Log.e("Layout", "Found layout");
            }catch (Exception e){
                Log.e("Layout", String.valueOf(e));
            }
        }

    }
}
