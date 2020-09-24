package com.mohammadreza.news.model;

//import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

public class UserModel implements Serializable {
    private int id;
    private String userName;
    private String password;
    private String email;
    private int isOnline;
    private String emailAddress;
    private String emailDisplayName;
    private String imageProfileUri = "";


//    public UserModel(FirebaseUser firebaseUser) {
//        this.id = firebaseUser.getUid().length();
//        this.emailDisplayName = firebaseUser.getDisplayName();
//        this.emailAddress = firebaseUser.getEmail();
//        this.imageProfileUri = String.valueOf(firebaseUser.getPhotoUrl());
//    }

    public UserModel(UserRealmModel userRealmModel) {
        this.id = userRealmModel.getId();
        this.userName = userRealmModel.getUserName();
        this.password = userRealmModel.getPassword();
        this.email = userRealmModel.getEmail();
        this.isOnline = userRealmModel.getIsOnline();
        this.emailAddress = userRealmModel.getEmailAddress();
        this.emailDisplayName = userRealmModel.getEmailDisplayName();
        this.imageProfileUri = userRealmModel.getImageUrl();
    }


    public UserModel() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailDisplayName() {
        return emailDisplayName;
    }

    public void setEmailDisplayName(String emailDisplayName) {
        this.emailDisplayName = emailDisplayName;
    }

    public String getImageProfileUri() {
        return imageProfileUri;
    }

    public void setImageProfileUri(String imageProfileUri) {
        this.imageProfileUri = imageProfileUri;
    }


}
