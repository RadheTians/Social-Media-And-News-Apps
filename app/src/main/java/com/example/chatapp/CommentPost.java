package com.example.chatapp;

import java.util.Date;

public class CommentPost {
    public String comment,name;
    public Date timestamp;
    public CommentPost(){

    }

    public CommentPost(String comment, String name, Date timestamp) {
        this.comment = comment;
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
