package com.example.r.mychat;

/**
 * Created by r on 1/10/2018.
 */

public class Userlast {

    private String Name;
    private String Image;
    private String Status;
    private String thumb_img;
    public  Userlast(){}

    public Userlast(String name, String image, String status) {
        Name = name;
        Image = image;
        Status = status;
    }

    public String getThumb_img() {
        return thumb_img;
    }

    public void setThumb_img(String thub_img) {
        this.thumb_img = thub_img;
    }

    public String getName() {
        return Name;

    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
