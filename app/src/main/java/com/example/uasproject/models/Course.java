package com.example.uasproject.models;

public class Course {

    public Course(){
//        Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    private String course_id, user_id, name, instructor, description, status, image;
    private Integer price;

    public Course(String course_id, String user_id, String name, String instructor, String description, String status, String image, Integer price) {
        this.course_id = course_id;
        this.user_id = user_id;
        this.name = name;
        this.instructor = instructor;
        this.description = description;
        this.status = status;
        this.image = image;
        this.price = price;
    }

    public String getCourse_id() {
        return course_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getInstructor() {
        return instructor;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public Integer getPrice() {
        return price;
    }

}
