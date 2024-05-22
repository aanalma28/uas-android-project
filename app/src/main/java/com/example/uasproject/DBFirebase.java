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



}
