package com.r0nin.etrasa;

import com.google.firebase.database.Exclude;
import com.google.type.LatLng;

public class Point {

    public LatLng latLng;
    public String description;
    public String userId;
    public String downloadUrl;
    public String imageId;
    @Exclude
    public User user;

    public Point(){

    }

    public Point(LatLng latLng, String description, String downloadUrl, String imageId){
        this.latLng = latLng;
        this.description = description;
        this.downloadUrl = downloadUrl;
    }

}
