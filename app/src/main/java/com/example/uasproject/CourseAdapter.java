package com.example.uasproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courseList;
    public CourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }
    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        try{
            Course course = courseList.get(position);
            if (course != null) {
                holder.titleCourse.setText(course.getName());
                holder.price.setText(String.valueOf(course.getPrice()));
                holder.user_id.setText(course.getUser_id());
                holder.descCourse.setText(course.getDescription());
            }
        }catch (Exception e){
            Log.e("Bind Error", String.valueOf(e));
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView titleCourse, price, user_id, descCourse ;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleCourse = itemView.findViewById(R.id.title_course);
            price = itemView.findViewById(R.id.price);
            user_id = itemView.findViewById(R.id.name_bimbel);
            descCourse = itemView.findViewById(R.id.desc_course);
        }
    }
}
