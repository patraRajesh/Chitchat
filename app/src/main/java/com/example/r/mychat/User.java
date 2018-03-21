package com.example.r.mychat;

import java.util.Date;

/**
 * Created by r on 12/28/2017.
 */

public class User {


    private String id;
    private String text;
    private String name;
    private long messageTime;


    public User() {
    }

    public User(String text, String name) {
        this.text = text;
        this.name = name;
        messageTime = new Date().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}