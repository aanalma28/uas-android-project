package com.example.uasproject.adapters;

import static java.security.AccessController.getContext;

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
import com.example.uasproject.utils.DBFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.CourseViewHolder> {
    private final List<Map<String, Object>> transactionList;
    private final RecycleViewInterface recycleViewInterface;
    private final Context context;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public TransactionAdapter(RecycleViewInterface recycleViewInterface, List<Map<String, Object>> transactionList){
        this.transactionList = transactionList;
        this.recycleViewInterface = recycleViewInterface;
        this.context = (Context) recycleViewInterface;

    }
    @NonNull
    @Override
    public TransactionAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_history, parent, false);
        return new CourseViewHolder(v, recycleViewInterface);
    }

    private Context getContext(View view) {
        return view.getContext();
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.CourseViewHolder holder, int position) {
        try{
            Map<String, Object> transaction = transactionList.get(position);
            String order_id = (String) transaction.get("order_id");
            String course_id = (String) transaction.get("course_id");
            String status = (String) transaction.get("transaction_status");

            Log.d("TransactionAdapter", transaction.toString());

            if (order_id == null || course_id == null) {
                Log.e("TransactionAdapter", "order_id or course_id is null");
                return;
            }

            mDatabase.child("course").child(course_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Log.d("CourseSnapshot", snapshot.toString());
                        String imgUrl = snapshot.child("image").getValue(String.class);
                        String user_id = snapshot.child("user_id").getValue(String.class);
                        String title = snapshot.child("name").getValue(String.class);
                        Log.d("TitleList", title);
                        assert user_id != null;
                        mDatabase.child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint({"ResourceAsColor", "SetTextI18n"})
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Log.d("UsersSnapshot", snapshot.toString());
                                    String name = snapshot.child("agency").getValue(String.class);
                                    Log.d("AgencyList", name);

                                    holder.titleCourse.setText(title);
                                    holder.bimbelName.setText(name);
                                    Glide.with(context).load(imgUrl).fitCenter().into(holder.imgCourse);

                                    if (status != null) {
                                        switch (status) {
                                            case "pending":
                                                holder.status.setText("Pending");
                                                holder.status.setTextColor(holder.itemView.getResources().getColor(R.color.yellow));
                                                break;
                                            case "settlement":
                                                holder.status.setText("Success");
                                                holder.status.setTextColor(holder.itemView.getResources().getColor(R.color.success));
                                                break;
                                            case "expire":
                                                holder.status.setText("Expired");
                                                holder.status.setTextColor(holder.itemView.getResources().getColor(R.color.alertred));
                                                break;
                                            default:
                                                holder.status.setText(status);
                                                break;
                                        }
                                    }
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

        }catch(Exception e){
            Log.e("TransactionAdapter", String.valueOf(e));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList != null ? transactionList.size() : 0;
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView titleCourse, bimbelName, status;
        public ImageView imgCourse;
        public CourseViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface) {
            super(itemView);
            try{
                titleCourse = itemView.findViewById(R.id.title_course);
                bimbelName = itemView.findViewById(R.id.name_bimbel);
                status = itemView.findViewById(R.id.status);
                imgCourse = itemView.findViewById(R.id.img_course);
            }catch(Exception e){
                Log.e("LayoutNotFound", String.valueOf(e));
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
