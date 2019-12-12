package com.example.plantglazing;

import com.google.firebase.firestore.Exclude;

public class Enquiry {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String location;
    private String enquirytext;
    private String DateSent;

    public Enquiry() {
        //public no-arg constructor needed
    }

    public Enquiry(String email, String enquirytext, String location, String name, String phone, String userId, String DateSent) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.enquirytext = enquirytext;
        this.DateSent = DateSent;
    }

    public String getuserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public String getEnquirytext() {
        return enquirytext;
    }
    public String getDateSent() {
        return DateSent;
    }
}