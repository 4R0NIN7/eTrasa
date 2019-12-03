package com.r0nin.etrasa;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

public class Point {
    public double lat, lng;
    public String title;
    public String description;
    public String userId;
    public int numer;
    public double radius;
    @Exclude
    public User user;

    public Point(){

    }

    public Point(String title, double lat, double lng, String description, String userId, int numer,double radius){
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.description = description;
        this.userId = userId;
        this.numer = numer;
        this.radius = radius;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public double getRadius() {
        return radius;
    }

    public int getNumer() {
        return numer;
    }
}
