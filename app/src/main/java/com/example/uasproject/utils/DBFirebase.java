package com.example.uasproject.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.uasproject.models.Bab;
import com.example.uasproject.models.Course;
import com.example.uasproject.models.Materi;
import com.example.uasproject.models.Seller;
import com.example.uasproject.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DBFirebase {
    private DatabaseReference mDatabase;
    public void addUser(String name, String email, String password, String id){
        try{
            String role = "Student";
            User user = new User(name, email, password, id, role);
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("users").child(id).setValue(user);

            Log.d("Write Data Success", "successfully");
        }catch(Exception e){
            Log.e("Write Data Error", String.valueOf(e));
        }
    }

    public DatabaseReference getUser(String id){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference data = mDatabase.child("users").child(id);
        return data;
    }

//    public void updatePassword(String id, String newPassword){
//        try{
//            Map<String, Object> update = new HashMap<>();
//            update.put("password", newPassword);
//
//            mDatabase = FirebaseDatabase.getInstance().getReference("users");
//            mDatabase.child(id).updateChildren(update);
//
//            Log.d("Update Password", "Update password successfully");
//        }catch(Exception e){
//            Log.d("Update Password", String.valueOf(e));
//        }
//    }

    public void editUserSeller(String agency, String address, String phone, String id){
        try{
            Map<String, Object> data = new HashMap<>();
            data.put("agency", agency);
            data.put("address", address);
            data.put("phone", phone);

            mDatabase = FirebaseDatabase.getInstance().getReference("users");
            mDatabase.child(id).updateChildren(data);

            Log.d("Edit Seller", "Edit seller successfully");
        }catch(Exception e){
            Log.e("Edit Seller", String.valueOf(e));
        }
    }

    public void updateUserSeller(String id, String agency, String phone, String address) {
        DatabaseReference data = getUser(id);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        String email = user.getEmail();
                        String password = user.getPassword();
                        String role = "Agency";
                        Seller seller = new Seller(id, agency, email, password, phone, address, role);

                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("users").child(id).setValue(seller).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Update to Seller", "Successfully updated to seller");
                            } else {
                                Log.e("Update to Seller", "Failed to update to seller", task.getException());
                            }
                        });
                    } else {
                        Log.e("User Not found", "User data is null");
                    }
                } else {
                    Log.e("User Not found", "User doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }


    public DatabaseReference getAllCourses(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference data = mDatabase.child("course");
        return data;
    }

    public void createCourse(String user_id, String name, String instructor, String description, String image, Integer price){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String course_id = mDatabase.child("course").push().getKey();
        String status = "close";
        Course course = new Course(course_id, user_id, name, instructor, description, status, image, price);
        try{
            mDatabase.child("course").child(course_id).setValue(course);
            Log.d("Create Course", "Course created successfully");
        }catch(Exception e){
            Log.e("Create Course", String.valueOf(e));
        }
    }


    public void updateCourse(String course_id, String name, String instructor, String description, String image, Integer price){
//        update course func
        mDatabase = FirebaseDatabase.getInstance().getReference("course");
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        data.put("instructor", instructor);
        data.put("price", price);
        data.put("image", image);

        mDatabase.child(course_id).updateChildren(data).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d("UpdateCourse", "Course updated successfully.");
            }else{
                Log.e("UpdateCourse", "Failed to update course.", task.getException());
            }
        });
    }

    public void deleteCourse(String course_id, OnCompleteListener<Void> onCompleteListener){
        try{
            mDatabase = FirebaseDatabase.getInstance().getReference("course");
            mDatabase.child(course_id).removeValue().addOnCompleteListener(onCompleteListener);

            Log.d("Delete Course", "Delete course successfully");
        }catch(Exception e){
            Log.e("Delete Course", String.valueOf(e));
        }
    }

    public void createBab(String course_id, String name, String detail){
        try{
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String bab_id = mDatabase.child("babs").push().getKey();

            Bab bab = new Bab(bab_id, course_id, name, detail);
            mDatabase.child("babs").child(bab_id).setValue(bab);

            Log.d("Create Bab", "Create bab successfully");
        }catch(Exception e){
            Log.e("Create Bab", String.valueOf(e));
        }
    }

    public void updateBab(String bab_id, String name, String detail){
//        This method make sure data is already in realtime db
//        Querying for make sure data exist, need more latency

//        mDatabase = FirebaseDatabase.getInstance().getReference("bab");
//        DatabaseReference data = mDatabase.child(bab_id);
//
//        data.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("name", name);
//                    data.put("description", detail);
//
//
//                    mDatabase.child(bab_id).updateChildren(data);
//
//                    Log.d("Update Bab", "Update bab successfully");
//                }else{
//                    Log.d("Update Bab", "Data not found");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("TAG", "Failed to read value.", error.toException());
//            }
//        });

//        This method directly update data without checking if data already exist
//        This method faster than above cause, no need query for search if data exist
//        Less latency

        try{
            mDatabase = FirebaseDatabase.getInstance().getReference("babs");

            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("detail", detail);

            mDatabase.child(bab_id).updateChildren(data);

            Log.d("Update Bab", "Update bab successfully");
        }catch(Exception e){
            Log.e("Update Bab", String.valueOf(e));
        }
    }

    public void deleteBab(String bab_id, OnCompleteListener<Void> onCompleteListener){
        try{
            mDatabase = FirebaseDatabase.getInstance().getReference("babs");
            mDatabase.child(bab_id).removeValue().addOnCompleteListener(onCompleteListener);

            Log.d("Delete Bab", "Delete bab successfully");
        }catch(Exception e){
            Log.e("Delete Bab", String.valueOf(e));
        }
    }

    public void createMateri(String bab_id, String course_id, String title, String content){
        try{
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String materi_id = mDatabase.child("materi").push().getKey();

            Materi materi = new Materi(materi_id, bab_id, course_id, title, content);
            mDatabase.child("materi").child(materi_id).setValue(materi);

            Log.e("Create Materi", "Create materi successfully");
        }catch(Exception e){
            Log.e("Create Materi", String.valueOf(e));
        }
    }

    public void updateMateri(String materi_id, String title, String content){
        try{
            mDatabase = FirebaseDatabase.getInstance().getReference("materi");
            Map<String, Object> data = new HashMap<>();
            data.put("title", title);
            data.put("content", content);

            mDatabase.child(materi_id).updateChildren(data);

            Log.e("Update Materi", "Update materi successfully");
        }catch(Exception e){
            Log.e("Update Materi", String.valueOf(e));
        }
    }

    public void deleteMateri(String materi_id, OnCompleteListener<Void> onCompleteListener) {
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference("materi");
            mDatabase.child(materi_id).removeValue().addOnCompleteListener(onCompleteListener);

            Log.e("Delete Materi", "Delete materi successfully");
        } catch (Exception e) {
            Log.e("Delete Materi", String.valueOf(e));
        }
    }

    public Query getSpecifyCourse(String user_id){
        mDatabase = FirebaseDatabase.getInstance().getReference("course");

        return mDatabase.orderByChild("user_id").equalTo(user_id);
    }

    public Query getSpecifyOpenCourse(){
        mDatabase = FirebaseDatabase.getInstance().getReference("course");

        return mDatabase.orderByChild("status").equalTo("open");
    }

    public Query getSpecifyCloseCourse(){
        mDatabase = FirebaseDatabase.getInstance().getReference("course");

        return mDatabase.orderByChild("status").equalTo("close");
    }

}
