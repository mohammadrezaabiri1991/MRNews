package com.mohammadreza.news.model;

//import com.google.firebase.auth.FirebaseUser;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class UserRealmModel extends RealmObject {

    @PrimaryKey
    private int id;

    private String userName;
    private String password;
    private String email;
    private int isOnline;

    private String emailDisplayName;
    private String emailAddress;

    private String imageUrl;


//    public UserRealmModel(FirebaseUser firebaseUser) {
//        this.emailDisplayName = firebaseUser.getDisplayName();
//        this.emailAddress = firebaseUser.getEmail();
//        this.imageUrl = String.valueOf(firebaseUser.getPhotoUrl());
//    }

    public UserRealmModel(UserModel userModel) {
        this.emailDisplayName = userModel.getEmailDisplayName();
//        this.emailAddress = userModel.getEmail();
        this.imageUrl = String.valueOf(userModel.getImageProfileUri());
    }

    public UserRealmModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
