package com.r0nin.etrasa;

import com.google.firebase.database.Exclude;

import java.util.Map;

public class Track {
    public Map<String, Point> points;
    public String userId;
    public String keyTrack;
    public String title;
    public String description;
    @Exclude
    public User user;

    public Track(){}

    public Track(String keyTrack, String userId, String title, Map<String, Point> points, String description){
        this.keyTrack = keyTrack;
        this.points = points;
        this.userId = userId;
        this.title = title;
        this.description = description;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getUserId(){
        return userId;
    }
    public Map<String, Point>  getPoints(){
        return points;
    }
}
