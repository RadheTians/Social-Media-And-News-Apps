package com.example.chatapp;

public class Users {

    public String name;
    public String image;
    public String status;
    public String thumbimage;



    public Users(){

    }

    public Users(String name, String image, String status, String thumbimage) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumbimage = thumbimage;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {

        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public String getThumbimage() {
        return thumbimage;
    }

    public void setThumb_image(String thumbimage) {

        this.thumbimage = thumbimage;
    }

}
