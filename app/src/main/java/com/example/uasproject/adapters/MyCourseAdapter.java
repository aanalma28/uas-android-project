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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.MyCourseViewHolder> {
    private final RecycleViewInterface recycleViewInterface;
    private final List<Map<String, Object>> courseList;
    private final Context context;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public MyCourseAdapter(RecycleViewInterface recycleViewInterface, List<Map<String, Object>> courseList, Context context) {
        this.recycleViewInterface = recycleViewInterface;
        this.courseList = courseList;
        this.context = context;
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
            Map<String, Object> course = courseList.get(position);
            String order_id = (String) course.get("order_id");
            String course_id = (String) course.get("course_id");

            if (order_id == null || course_id == null) {
                Log.e("TransactionAdapter", "order_id or course_id is null");
                return;
            }

            mDatabase.child("order").orderByChild("order_id").equalTo(order_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("TransactionData", "DataSnapshot count: " + snapshot.getChildrenCount());
                    if(snapshot.exists()){
                        mDatabase.child("course").child(course_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String imgUrl = snapshot.child("image").getValue(String.class);
                                    String user_id = snapshot.child("user_id").getValue(String.class);
                                    String title = snapshot.child("name").getValue(String.class);
                                    String desc = snapshot.child("description").getValue(String.class);

                                    assert user_id != null;
                                    mDatabase.child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                String name = snapshot.child("agency").getValue(String.class);
                                                holder.titleCourse.setText(title);
                                                holder.user_id.setText(name);
                                                holder.descCourse.setText(desc);
                                                Glide.with(context).load(imgUrl).fitCenter().into(holder.img_course);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("GetUserTransaction", String.valueOf(error));
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("GetCourseTransaction", String.valueOf(error));
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("GetTransaction", String.valueOf(error));
                }
            });

        }catch(Exception e){
            Log.e("TransactionAdapter", String.valueOf(e));
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
