package com.example.doancuoiky.Models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String phone;
    private String birthday;
    private String imgProfileUrl;
    private Map<String, Object> habits;

    public Map<String, Object> getHabits() {
        return habits;
    }

    public void setHabits(Map<String, Object> habits) {
        this.habits = habits;
    }

    public User() {}

    public User(String username, String phone, String birthday, String imgProfileUrl) {
        this.username = username;
        this.phone = phone;
        this.birthday = birthday;
        this.imgProfileUrl = imgProfileUrl;
        this.habits = new HashMap<>();
    }

    public User(String username, String phone, String birthday, String imgProfileUrl, Map<String, Object> habits) {
        this.username = username;
        this.phone = phone;
        this.birthday = birthday;
        this.imgProfileUrl = imgProfileUrl;
        this.habits = habits;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getImgProfileUrl() {
        return imgProfileUrl;
    }

    public void setImgProfileUrl(String imgProfileUrl) {
        this.imgProfileUrl = imgProfileUrl;
    }
}
