package com.example.economicgrowthapp.logins;

public class Information {
    private String Email;
    private String Name;
    //constructor, getters and setters
    public Information() {

    }

    public Information(String email, String name) {
        Email = email;
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}