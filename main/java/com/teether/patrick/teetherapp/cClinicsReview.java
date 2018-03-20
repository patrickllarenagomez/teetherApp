package com.teether.patrick.teetherapp;

/**
 * Created by Patrick on 2/8/2018.
 */

public class cClinicsReview {

    private String fullName;
    private String comment;
    private int rating;

    public cClinicsReview(String fullName, String comment, int rating) {
        this.fullName = fullName;
        this.comment = comment;
        this.rating = rating;
    }



    public String getFullName() {
        return fullName;
    }

    public String getComment() {
        return comment;
    }

    public int getRating() {
        return rating;
    }







}
