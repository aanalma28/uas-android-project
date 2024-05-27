package com.example.uasproject;

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

import java.text.NumberFormat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private final RecycleViewInterface recycleViewInterface;
    private List<Course> courseList;

    public CourseAdapter(List<Course> courseList, RecycleViewInterface recycleViewInterface) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(v, recycleViewInterface);
    }

    private Context getContext(View view) {
        return view.getContext();
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        try{
            Course course = courseList.get(position);
            DBFirebase db = new DBFirebase();
            DatabaseReference data = db.getUser(course.getUser_id());

            Log.d("AdapterCourse", course.getName());

            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Seller seller = snapshot.getValue(Seller.class);
                        String name = seller.getAgency();
                        String imgUrl = course.getImage();

                        Locale localeID = new Locale("in", "ID");
                        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                        String formattedPrice = formatRupiah.format(course.getPrice());
                        formattedPrice = formattedPrice.replace("Rp", "Rp. ").replace(",00", "");
                        String finalFormattedPrice = formattedPrice;

                        if (course != null) {
                            holder.titleCourse.setText(course.getName());
                            holder.price.setText(finalFormattedPrice);
                            holder.user_id.setText(name);
                            holder.descCourse.setText(course.getDescription());
                            Glide.with(getContext(holder.itemView)).load(imgUrl).fitCenter().into(holder.img_course);
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

//    Search Course function
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Course> newList) {
        courseList = newList;
        notifyDataSetChanged();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView titleCourse, price, user_id, descCourse;

        public ImageView img_course;

        public CourseViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            titleCourse = itemView.findViewById(R.id.title_course);
            price = itemView.findViewById(R.id.price);
            user_id = itemView.findViewById(R.id.name_bimbel);
            descCourse = itemView.findViewById(R.id.desc_course);
            img_course = itemView.findViewById(R.id.img_course);

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
