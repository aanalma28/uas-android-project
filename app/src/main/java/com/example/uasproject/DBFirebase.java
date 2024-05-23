package com.example.uasproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    public void updatePassword(String id, String newPassword){
        Map<String, Object> update = new HashMap<>();
        update.put("password", newPassword);

        mDatabase = FirebaseDatabase.getInstance().getReference("course");
        mDatabase.child(id).updateChildren(update);
    }

    public void editUserSeller(String agency, String address, String phone, String id){
        Map<String, Object> data = new HashMap<>();
        data.put("agency", agency);
        data.put("address", address);
        data.put("phone", phone);

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.child(id).updateChildren(data);
    }

    public void updateUserSeller(String id, String agency, String phone, String address){
        DatabaseReference data = getUser(id);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user = snapshot.getValue(User.class);

                    String email = user.getEmail();
                    String password = user.getPassword();
                    String role = "Seller";
                    Seller seller = new Seller(id, agency, email, password, phone, address, role);

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").child(id).setValue(seller);

                    Log.d("Update to Seller", "Successfully update to seller");
                }else{
                    Log.e("User Not found", "User doesnt exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
//
//        Seller seller = new Seller(id, agency, email, password, phone, address);
//        Map<String, Object> sellerValue = seller.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("users/"+id+"/", sellerValue);
//
//        mDatabase.updateChildren(childUpdates);
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


}
