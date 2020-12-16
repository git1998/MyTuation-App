package com.example.minglishmantra_beta;

import android.app.Application;
import android.net.Uri;

import java.util.ArrayList;

public class ApplicationClass extends Application {

    String userName , imageUrl ,mobileNumber;
    public ArrayList<String> enrolledCourse =new ArrayList<>();

    public ArrayList<String> getEnrolledCourse() {
        return enrolledCourse;
    }

    public void setEnrolledCourse(ArrayList<String> enrolledCourse) {
        this.enrolledCourse = enrolledCourse;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
