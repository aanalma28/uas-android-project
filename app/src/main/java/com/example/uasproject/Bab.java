package com.example.uasproject;

public class Bab {
    private String bab_id;
    private String course_id;
    private String name;
    private String detail;

    public Bab(){
//        Firebase Datasnapshot
    }

    public Bab(String bab_id, String course_id, String name, String detail){
        this.bab_id = bab_id;
        this.course_id = course_id;
        this.name = name;
        this.detail = detail;
    }

    public String getBab_id(){
        return bab_id;
    }

    public void setBab_id(String bab_id){
        this.bab_id = bab_id;
    }

    public String getCourse_id(){
        return course_id;
    }

    public void setCourse_id(String course_id){
        this.course_id = course_id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDetail(){
        return detail;
    }

    public void setDetail(String detail){
        this.detail = detail;
    }
}
