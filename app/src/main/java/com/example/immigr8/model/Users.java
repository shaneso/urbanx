package com.example.immigr8.model;

import java.io.Serializable;

public class Users implements Serializable {
    private String id;
    private String username;
    private String imageURL;
    private String status;
    private String location;
    private String immigratedFrom;
    private String age;
    private String gender;
    private String dateJoined;
    private String bio;
    private String timeAsImmigrant;

    public Users() {
    }

    public Users(String id, String username, String imageURL, String status, String location,
                 String immigratedFrom, String age, String gender, String dateJoined,
                 String bio, String timeAsImmigrant) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.location = location;
        this.immigratedFrom = immigratedFrom;
        this.age = age;
        this.gender = gender;
        this.dateJoined = dateJoined;
        this.bio = bio;
        this.timeAsImmigrant = timeAsImmigrant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getImmigratedFrom() {
        return immigratedFrom;
    }

    public void setImmigratedFrom(String immigratedFrom) {
        this.immigratedFrom = immigratedFrom;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTimeAsImmigrant() {
        return timeAsImmigrant;
    }

    public void setTimeAsImmigrant(String timeAsImmigrant) {
        this.timeAsImmigrant = timeAsImmigrant;
    }
}
