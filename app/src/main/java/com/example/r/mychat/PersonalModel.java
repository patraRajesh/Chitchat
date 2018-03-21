package com.example.r.mychat;

/**
 * Created by r on 1/14/2018.
 */

public class PersonalModel {
    private String message;
    private Boolean seen;
    private  long time;
    private String type;
    private String from;
    private String fro;


    public PersonalModel(){}

    public PersonalModel(String fro) {
        this.fro = fro;
    }

    public String getFro() {
        return fro;
    }

    public void setFro(String fro) {
        this.fro = fro;
    }

    public PersonalModel(String from, String message, long time, String type) {
        this.message = message;
        this.time = time;
        this.type = type;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PersonalModel(Boolean seen) {
        this.seen = seen;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


}
