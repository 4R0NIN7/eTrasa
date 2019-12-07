package com.r0nin.etrasa;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String uid;
    public String displayName;
    public String token;
    public String email;
    public String imageDownloadUri;

    public User(){}

    public User(String email,String uid, String displayName, String token){
        this.email = email;
        this.uid = uid;
        this.displayName = displayName;
        this.token = token;
        this.imageDownloadUri = "";
    }

    public String getUid() {
        return uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImageDownloadUri() {
        return imageDownloadUri;
    }
}
