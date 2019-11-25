package com.jesper.shutapp.model;

public class User {
    private String name;
    private String email;
    private String profile_picture;
    private String uid;
    private String bio;
    private String age;
    private String gender;
    private String status = "offline";

    public User(String name, String email, String profile_picture,String uid,String bio, String status, String gender, String age) {
        this.name = name;
        this.email = email;
        this.profile_picture = profile_picture;
        this.uid = uid;
        this.bio = bio;
        this.age = age;
        this.gender = gender;
        this.status = status;
    }

    public User() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", uid='" + uid + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
