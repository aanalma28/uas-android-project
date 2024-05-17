package com.example.uasproject;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DBFirebase {
    private DatabaseReference mDatabase;
    public void addUser(String name, String email, String password, String id){
        HashMap<String, Object> userHashmap = new HashMap<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        userHashmap.put("id", id);
        userHashmap.put("name", name);
        userHashmap.put("email", email);
        userHashmap.put("password", password);

        mDatabase.child("users").child(id).setValue(userHashmap);
    }

    public DatabaseReference getUser(String id){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference data = mDatabase.child("users").child(id);
        return data;
    }

}
