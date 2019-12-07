package com.r0nin.etrasa;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Track {
    public Map<String, Point> points;
    public String userId;
    public String keyTrack;
    public String title;
    public String description;
    public String displayName;
    public float sumOfRates;
    public float rating;
    public int howMuchPeople;
    public Map<String, Float> usersWhichHaveRated;

    public String rated;

    public Track(){}

    public Track(String keyTrack, String userId, String title, Map<String, Point> points, String description, String displayName, Map<String, Float> usersWhichHaveRated){
        this.keyTrack = keyTrack;
        this.points = points;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.displayName = displayName;
        this.sumOfRates = 0;
        this.howMuchPeople = 0;
        this.rating = 0;
        this.usersWhichHaveRated = usersWhichHaveRated;
    }

    public void setSumOfRates(float sumOfRates){
        this.sumOfRates = sumOfRates;
    }

    public float getSumOfRates() {
        return sumOfRates;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setHowMuchPeople(int howMuchPeople) {
        this.howMuchPeople = howMuchPeople;
    }

    public int getHowMuchPeople() {
        return howMuchPeople;
    }

    public float getRating() {
        return rating;
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

    public String getKeyTrack() {
        return keyTrack;
    }

    public String getDisplayName() {
        return displayName;
    }
    public String getRated(){
        return rated;
    }

    public Map<String, Float> getUsersWhichHaveRated() {
        return usersWhichHaveRated;
    }
}
