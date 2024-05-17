package com.example.uasproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AdapterCourse extends RecyclerView.Adapter<AdapterCourse.MyViewHolder> {
    private List mlist;
    private Activity activity;
    DatabaseReference mDatabase;

    public AdapterCourse(List mList, Activity activity){
        this.mlist = mList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AdapterCourse.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View viewItem = inflater.inflate(R.layout.layout_item_course, parent, false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCourse.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView card_result;
        TextView title_bimbel, name_bimbel, desc_course, price;
        ImageView img_course;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_result = itemView.findViewById(R.id.card_result);
            title_bimbel = itemView.findViewById(R.id.title_course);
            name_bimbel = itemView.findViewById(R.id.name_bimbel);
            desc_course = itemView.findViewById(R.id.desc_course);
            price = itemView.findViewById(R.id.price);
            img_course = itemView.findViewById(R.id.img_course);
        }
    }
}
