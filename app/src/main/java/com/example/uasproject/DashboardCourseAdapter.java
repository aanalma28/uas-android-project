package com.example.uasproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DashboardCourseAdapter extends RecyclerView.Adapter<DashboardCourseAdapter.CourseViewHolder>{
    private final RecycleViewInterface recycleViewInterface;
    private final List<Course> courseList;
    private final Context context;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("babs");

    public DashboardCourseAdapter(RecycleViewInterface recycleViewInterface, List<Course> courseList) {
        this.recycleViewInterface = recycleViewInterface;
        this.courseList = courseList;
        this.context = (Context) recycleViewInterface;
    }

    @NonNull
    @Override
    public DashboardCourseAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_in_dashboard, parent, false);
        return new CourseViewHolder(v, recycleViewInterface);
    }

    private Context getContext(View view) {
        return view.getContext();
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardCourseAdapter.CourseViewHolder holder, int position) {
        try{
            Course course = courseList.get(position);
            String imgUrl = course.getImage();

            mDatabase.orderByChild("course_id").equalTo(course.getCourse_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("Course Data", "DataSnapshot count: " + snapshot.getChildrenCount());
                    if(snapshot.exists()){
                        holder.title.setText(course.getName());
                        holder.jumlah_bab.setText(snapshot.getChildrenCount() + " Bab");
                        holder.description.setText(course.getDescription());
                        Glide.with(getContext(holder.itemView)).load(imgUrl).fitCenter().into(holder.img_course);

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
        return courseList.size() ;
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView title, jumlah_bab, description;
        public ImageView img_course;
        public CourseViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface){
            super(itemView);
            try {
                title = itemView.findViewById(R.id.title_course);
                jumlah_bab = itemView.findViewById(R.id.jumlah_bab);
                description = itemView.findViewById(R.id.desc_course);
                img_course = itemView.findViewById(R.id.img_course);

                Log.e("Layout", "Found layout");
            }catch (Exception e){
                Log.e("Layout", String.valueOf(e));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recycleViewInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            recycleViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
