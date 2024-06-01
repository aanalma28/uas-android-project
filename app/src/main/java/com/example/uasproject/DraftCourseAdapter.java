package com.example.uasproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DraftCourseAdapter extends RecyclerView.Adapter<DraftCourseAdapter.CourseViewHolder> {
    private final RecycleViewInterface recycleViewInterface;
    private List<Course> courseList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("course");
    @SuppressLint("NotifyDataSetChanged")
    public DraftCourseAdapter(List<Course> courseList, RecycleViewInterface recycleViewInterface) {
        this.courseList = courseList;
        this.recycleViewInterface = recycleViewInterface;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_draft, parent, false);
        return new CourseViewHolder(v, recycleViewInterface);
    }

    private Context getContext(View view) {
        return view.getContext();
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        try{
            Course course = courseList.get(position);
            String user_id = mAuth.getCurrentUser().getUid();
            String imgUrl = course.getImage();
            DatabaseReference babsRef = FirebaseDatabase.getInstance().getReference("babs");

            mDatabase.orderByChild("user_id").equalTo(user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("Course Data", "DataSnapshot count: " + snapshot.getChildrenCount());
                    if(snapshot.exists()){
                        babsRef.orderByChild("course_id").equalTo(course.getCourse_id()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot babSnapshot) {
                                String nama = course.getName();
                                String desc = course.getDescription();
                                String instructor = course.getInstructor();
                                String price = course.getPrice().toString();
                                String image = course.getImage();
                                String id = course.getCourse_id();
                                int babCount = (int) babSnapshot.getChildrenCount();

                                Log.d("Course Data", "DataSnapshot: " + course.toString());

                                holder.title.setText(course.getName());
                                holder.jumlah_bab.setText(babCount + " Bab");
                                holder.description.setText(course.getDescription());
                                Glide.with(getContext(holder.itemView)).load(imgUrl).fitCenter().into(holder.img_course);

                                holder.btn_publish.setOnClickListener(v -> {
                                    ConstraintLayout alertConstrainLayout = holder.itemView.findViewById(R.id.alert_constrain_layout);
                                    View view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.alert_dialog, alertConstrainLayout, false);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                    builder.setView(view);

                                    final AlertDialog alertDialog = builder.create();

                                    Button btnNo = view.findViewById(R.id.alertNo);
                                    Button btnDone = view.findViewById(R.id.alertDone);
                                    ImageView imgView = view.findViewById(R.id.centeredImageView);
                                    TextView publishTxt = view.findViewById(R.id.alertTitle);
                                    TextView publishDesc = view.findViewById(R.id.alertDesc);

                                    imgView.setImageResource(R.drawable.info);
                                    publishTxt.setText("Publish");
                                    publishDesc.setText("Kamu yakin ingin publish modul ini?");
                                    btnDone.setBackgroundResource(R.drawable.btn_info);
                                    btnDone.setText("Publish");

                                    LinearLayout wrapper = view.findViewById(R.id.layout_loading);
                                    ConstraintLayout layoutDialog = view.findViewById(R.id.layout_dialog);
                                    ProgressBar progressBar = view.findViewById(R.id.progressBar);

                                    btnNo.setOnClickListener(v1 -> {
                                        alertDialog.dismiss();
                                    });

                                    btnDone.setOnClickListener(v1 -> {
                                        if (babSnapshot.exists()) {
                                            wrapper.setVisibility(View.VISIBLE);
                                            layoutDialog.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.VISIBLE);
                                            progressBar.setIndeterminate(true);
                                            progressBar.setIndeterminateTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.bluePrimary)));

                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("course");
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("status", "open");

                                            db.child(id).updateChildren(data).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    ConstraintLayout successConstrainLayout = holder.itemView.findViewById(R.id.success_constrain_layout);
                                                    View viewSuccess = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.success_dialog, successConstrainLayout, false);

                                                    AlertDialog.Builder builderSuccess = new AlertDialog.Builder(holder.itemView.getContext());
                                                    builderSuccess.setView(viewSuccess);

                                                    final AlertDialog alertDialogSuccess = builderSuccess.create();

                                                    TextView successTitle = viewSuccess.findViewById(R.id.successTitle);
                                                    TextView successDesc = viewSuccess.findViewById(R.id.successDesc);
                                                    Button tutup = viewSuccess.findViewById(R.id.successDone);

                                                    successDesc.setText("Modul berhasil di publish !");

                                                    tutup.setOnClickListener(v2 -> {
                                                        alertDialogSuccess.dismiss();
                                                    });

                                                    alertDialog.dismiss();
                                                    alertDialogSuccess.show();
                                                } else {
                                                    Toast.makeText(holder.itemView.getContext(), "Oops... Sepertinya ada yang salah", Toast.LENGTH_SHORT).show();
                                                    alertDialog.dismiss();
                                                }
                                            });
                                        }else {
                                            Toast.makeText(holder.itemView.getContext(), "Tambahkan bab terlebih dahulu", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                });

                                holder.btnDelete.setOnClickListener(v -> {
                                    ConstraintLayout alertConstrainLayout = holder.itemView.findViewById(R.id.alert_constrain_layout);
                                    View view = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.alert_dialog, alertConstrainLayout, false);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                    builder.setView(view);

                                    final AlertDialog alertDialog = builder.create();

                                    ConstraintLayout successConstrainLayout = holder.itemView.findViewById(R.id.success_constrain_layout);
                                    View viewSuccess = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.success_dialog, successConstrainLayout, false);

                                    AlertDialog.Builder builderSuccess = new AlertDialog.Builder(holder.itemView.getContext());
                                    builderSuccess.setView(viewSuccess);

                                    final AlertDialog alertDialogSuccess = builderSuccess.create();

                                    Button btnNo = view.findViewById(R.id.alertNo);
                                    Button btnDone = view.findViewById(R.id.alertDone);
                                    LinearLayout wrapper = view.findViewById(R.id.layout_loading);
                                    ConstraintLayout layoutDialog = view.findViewById(R.id.layout_dialog);
                                    ProgressBar progressBar = view.findViewById(R.id.progressBar);
                                    TextView alertDesc = view.findViewById(R.id.alertDesc);

                                    alertDesc.setText("Jika course dihapus maka bab, materi dan quiz yang ada di course ini otomatis akan dihapus. Yakin hapus ?");

                                    btnNo.setOnClickListener(v1 -> {
                                        alertDialog.dismiss();
                                    });

                                    btnDone.setOnClickListener(v1 -> {
                                        wrapper.setVisibility(View.VISIBLE);
                                        layoutDialog.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.VISIBLE);
                                        progressBar.setIndeterminate(true);
                                        progressBar.setIndeterminateTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.bluePrimary)));

                                        DBFirebase db = new DBFirebase();
                                        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

                                        // Flag to track if any delete operation fails
                                        AtomicBoolean deleteFailed = new AtomicBoolean(false);

                                        // Delete materi
                                        dbReference.child("materi").orderByChild("course_id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    if(dataSnapshot.exists()){
                                                        dataSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                                            Log.d("DeleteMateri", "Delete Materi Successfully");

                                                        }).addOnFailureListener(e -> {
                                                            Log.e("DeleteMateri", String.valueOf(e));
                                                            deleteFailed.set(true);  // Set the flag to true if deletion fails
                                                            // Show error message and stop further execution
                                                            Toast.makeText(holder.itemView.getContext(), "Gagal menghapus materi", Toast.LENGTH_SHORT).show();
                                                            progressBar.setVisibility(View.GONE);
                                                            layoutDialog.setVisibility(View.GONE);
                                                        });
                                                        if (deleteFailed.get()) break; // Stop if any delete operation fails
                                                    }
                                                }

                                                if (!deleteFailed.get()) {
                                                    // Delete babs
                                                    dbReference.child("babs").orderByChild("course_id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot dataSnapshots : snapshot.getChildren()){
                                                                if(dataSnapshots.exists()){
                                                                    dataSnapshots.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                                                        Log.d("DeleteBab", "Delete bab successfully");

                                                                    }).addOnFailureListener(e -> {
                                                                        Log.e("DeleteBab", String.valueOf(e));
                                                                        deleteFailed.set(true);  // Set the flag to true if deletion fails
                                                                        // Show error message and stop further execution
                                                                        Toast.makeText(holder.itemView.getContext(), "Gagal menghapus bab", Toast.LENGTH_SHORT).show();
                                                                        progressBar.setVisibility(View.GONE);
                                                                        layoutDialog.setVisibility(View.GONE);
                                                                    });
                                                                    if (deleteFailed.get()) break; // Stop if any delete operation fails
                                                                }
                                                            }

                                                            if (!deleteFailed.get()) {
                                                                // Delete course
                                                                db.deleteCourse(id, new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            Button tutup = viewSuccess.findViewById(R.id.successDone);

                                                                            String[] parts = image.split("/");
                                                                            String lastPart = parts[parts.length - 1];
                                                                            String fileName = lastPart.split("\\?")[0];

                                                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                                                            StorageReference storageRef = storage.getReference();
                                                                            String file = fileName.replace("%2F", "/");
                                                                            StorageReference path = storageRef.child(file);
                                                                            path.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Log.d("Delete File", "Successfully delete File");
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.e("Delete File", String.valueOf(e));
                                                                                }
                                                                            });

                                                                            tutup.setOnClickListener(v2 -> {
                                                                                alertDialogSuccess.dismiss();
                                                                                Context context = holder.itemView.getContext();
                                                                                if(context instanceof Activity){
                                                                                    ((Activity) context).finish();
                                                                                }
                                                                            });

                                                                            alertDialog.dismiss();
                                                                            alertDialogSuccess.show();
                                                                        } else {
                                                                            Toast.makeText(holder.itemView.getContext(), "Oops... Sepertinya ada yang salah", Toast.LENGTH_SHORT).show();
                                                                            alertDialog.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.e("DBDeleteError", "Failed to read data.", error.toException());
                                                            Toast.makeText(holder.itemView.getContext(), "Gagal membaca data bab", Toast.LENGTH_SHORT).show();
                                                            progressBar.setVisibility(View.GONE);
                                                            layoutDialog.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("DBDeleteError", "Failed to read data.", error.toException());
                                                Toast.makeText(holder.itemView.getContext(), "Gagal membaca data materi", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                layoutDialog.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    });

                                    alertDialog.show();
                                });

                                holder.btnEdit.setOnClickListener(v -> {
                                    Intent intent = new Intent(getContext(holder.itemView), EditCourseActivity.class);
                                    intent.putExtra("name", nama);
                                    intent.putExtra("desc", desc);
                                    intent.putExtra("instructor", instructor);
                                    intent.putExtra("price", price);
                                    intent.putExtra("image", image);
                                    intent.putExtra("course_id", id);
                                    getContext(holder.itemView).startActivity(intent);
                                });

                                Log.d("Course Data", "Course found");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.w("Course Data", "Failed to read value.", error.toException());
                            }
                        });
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
        public TextView title, jumlah_bab, description, btn_publish;
        public ImageView img_course;
        public ImageView btnEdit, btnDelete;
        public CourseViewHolder(@NonNull View itemView, RecycleViewInterface recycleViewInterface){
            super(itemView);
            try {
                title = itemView.findViewById(R.id.title_course);
                jumlah_bab = itemView.findViewById(R.id.jumlah_bab);
                description = itemView.findViewById(R.id.desc_course);
                img_course = itemView.findViewById(R.id.img_course);
                btn_publish = itemView.findViewById(R.id.btn_publish);
                btnEdit = itemView.findViewById(R.id.edit_course);
                btnDelete = itemView.findViewById(R.id.delete_course);

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
