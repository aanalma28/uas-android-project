package com.example.uasproject;

public class Materi {
    private String materi_id;
    private String bab_id;
    private String title;
    private String description;
    public Materi(){
//        Firebase Datasnapshot
    }

    public Materi(String materi_id, String bab_id, String title, String description){
        this.materi_id = materi_id;
        this.bab_id = bab_id;
        this.title = title;
        this.description = description;
    }

    public String getMateri_id(){
        return materi_id;
    }

    public void setMateri_id(String materi_id){
        this.materi_id = materi_id;
    }

    public String getBab_id(){
        return bab_id;
    }

    public void setBab_id(String bab_id){
        this.bab_id = bab_id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
