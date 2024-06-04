package com.example.uasproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Materi implements Parcelable {
    private String materi_id;
    private String bab_id;
    private String course_id;
    private String title;
    private String content;
    public Materi(){
//        Firebase Datasnapshot
    }

//    Parcel makes object can passing data in intent while starting activity
    protected Materi(Parcel in){
        materi_id = in.readString();
        course_id = in.readString();
        bab_id = in.readString();
        title = in.readString();
        content = in.readString();
    }

    public static final Creator<Materi> CREATOR = new Creator<Materi>() {
        @Override
        public Materi createFromParcel(Parcel source) {
            return new Materi(source);
        }

        @Override
        public Materi[] newArray(int size) {
            return new Materi[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(materi_id);
        dest.writeString(course_id);
        dest.writeString(bab_id);
        dest.writeString(title);
        dest.writeString(content);
    }

    public Materi(String materi_id, String bab_id, String course_id, String title, String content){
        this.materi_id = materi_id;
        this.bab_id = bab_id;
        this.course_id = course_id;
        this.title = title;
        this.content = content;
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
    public String getCourse_id(){
        return course_id;
    }
    public void setCourse_id(String course_id){
        this.course_id = course_id;
    }
    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }
}
