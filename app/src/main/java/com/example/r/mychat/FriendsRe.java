package com.example.r.mychat;

/**
 * Created by r on 1/11/2018.
 */

public class FriendsRe {

    public FriendsRe(){}

   public String request_type;

    public FriendsRe(String request_type) {
        this.request_type = request_type;
    }

    public String getDate() {
        return request_type;
    }

    public void setDate(String date) {
        this.request_type = date;
    }
}
