package com.example.uasproject;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Seller {
    private String id, agency, email, password, phone, address, role;

    public Seller(){
//        Default constructor required for calls to DataSnapshot.getValue(Seller.class)
    }

    public Seller(String id, String agency, String email, String password, String phone, String address, String role){
        this.id = id;
        this.agency = agency;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getAgency(){
        return agency;
    }

    public void setAgency(String agency){
        this.agency = agency;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(){
        this.password = password;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getRole(){
        return role;
    }

    public void setRole(String role){
        this.role = role;
    }

//    @Exclude
//    public Map<String, Object> toMap(){
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("id", id);
//        result.put("agency", agency);
//        result.put("email", email);
//        result.put("password", password);
//        result.put("phone", phone);
//        result.put("address", address);
//
//        return result;
//    }
}
