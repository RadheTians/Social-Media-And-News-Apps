package com.example.chatapp;

import com.google.firebase.database.Exclude;

import androidx.annotation.NonNull;

public class BlogPostId {

    @Exclude
    public String BlogPostId;

    public <T extends BlogPostId> T withId(@NonNull final String id) {
        this.BlogPostId = id;
        return (T) this;
    }

}
