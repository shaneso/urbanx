package com.example.immigr8.model;

import java.util.ArrayList;
import java.io.Serializable;

public class CompleteUser implements Serializable {
    private Users user;
    private ArrayList<String> hobbies;
    private ArrayList<String> likes;
    private ArrayList<String> values;
    private ArrayList<String> languages;

    public CompleteUser(){}

    public CompleteUser(Users user, ArrayList<String> hobbies, ArrayList<String> languages,
                        ArrayList<String> values, ArrayList<String> likes) {
        this.user = user;
        this.hobbies = hobbies;
        this.likes = likes;
        this.values = values;
        this.languages = languages;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public ArrayList<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(ArrayList<String> hobbies) {
        this.hobbies = hobbies;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<String> languages) {
        this.languages = languages;
    }
}
