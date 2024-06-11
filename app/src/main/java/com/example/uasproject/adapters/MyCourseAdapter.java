package com.example.uasproject.adapters;

import android.annotation.SuppressLint;
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
import com.example.uasproject.R;
import com.example.uasproject.RecycleViewInterface;
import com.example.uasproject.models.Course;
import com.example.uasproject.models.Seller;
import com.example.uasproject.utils.DBFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.MyCourseViewHolder> {
    private final RecycleViewInterface recycleViewInterface;
    private final List<Course> courseList;

    public MyCourseAdapter(RecycleViewInterface recycleViewInterface, List<Course> courseList, Context context) {
        this.recycleViewInterface = recycleViewInterface;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public MyCourseAdapter.MyCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mycourse, parent, false);
        return new MyCourseViewHolder(v, recycleViewInterface);
    }

    private Context getContext(View view) {
        return view.getContext();
    }

    @Override
    public void onBindViewHolder(@NonNull MyCourseAdapter.MyCourseViewHolder holder, int position) {
        try{
            Course course = courseList.get(position);
            DBFirebase db = new DBFirebase();
            DatabaseReference data = db.getUser(course.getUser_id());

            Log.d("AdapterMyCourse", course.getName());

            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Seller seller = snapshot.getValue(Seller.class);
                        assert seller != null;
                        String name = seller.getAgency();
                        String imgUrl = course.getImage();


                        holder.titleCourse.setText(course.getName());
                        holder.user_id.setText(name);
                        holder.descCourse.setText(course.getDescription());
                        if (holder.itemView.getContext() != null) {
                            Glide.with(holder.itemView.getContext()).load(imgUrl).fitCenter().into(holder.img_course);
                        }
                    }else{
                        Log.e("User Not found", "User doesnt exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("TAG", "Failed to read value.", error.toException());
                }
            });

        }catch (Exception e){
            Log.e("Bind Error", String.valueOf(e));
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class MyCourseViewHolder extends RecyclerView.ViewHolder {
        public TextView titleCourse, user_id, descCourse;

        public ImageView img_course;

        public MyCourseViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            titleCourse = itemView.findViewById(R.id.title_course);
            user_id = itemView.findViewById(R.id.name_bimbel);
            descCourse = itemView.findViewById(R.id.desc_course);
            img_course = itemView.findViewById(R.id.img_course);

            itemView.setOnClickListener(v -> {
                if (recycleViewInterface != null){
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION){
                        recycleViewInterface.onItemClick(pos);
                    }
                }
            });
        }
    }
}
