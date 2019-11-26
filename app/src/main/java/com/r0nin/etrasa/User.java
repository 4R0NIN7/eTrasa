package com.r0nin.etrasa;

public class User {
    protected String userName, email;
    protected int age;

    public User(){}

    public User(String userName, String email, int age){
        this.userName = userName;
        this.email = email;
        this.age = age;
    }
}
