package com.r0nin.etrasa;

import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Map;

public class Track {
    public Map<String, Point> points;
    public String userId;
    public String keyTrack;
    public String title;
    @Exclude
    public User user;
    public Track(String keyTrack, String userId, String title, Map<String, Point> points){
        this.keyTrack = keyTrack;
        this.points = points;
        this.userId = userId;
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
    public String getUserId(){
        return title;
    }
    public Map<String, Point>  getPoints(){
        return points;
    }
}
