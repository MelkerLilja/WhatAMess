package com.jesper.shutapp.model;

public class User {
    private String name;
    private String email;
    private String profile_picture;

    public User(String name, String email, String profile_picture) {
        this.name = name;
        this.email = email;
        this.profile_picture = profile_picture;
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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                '}';
    }
}
