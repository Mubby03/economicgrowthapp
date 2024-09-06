package com.example.economicgrowthapp.logins;

public class User {
    private String profilePicture;
    private String eMail;

    public User() {

    }

    public User(String profilePicture, String eMail) {
        this.profilePicture = profilePicture;
        this.eMail = eMail;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
}
