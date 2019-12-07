package com.r0nin.etrasa;

public class Image {

    public String imageKey;
    public String userId;
    public String downloadUrl;

    public Image(){}

    public Image(String imageKey, String userId, String downloadUrl){
        this.imageKey = imageKey;
        this.userId = userId;
        this.downloadUrl = downloadUrl;
    }


    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getImageKey() {
        return imageKey;
    }
}
