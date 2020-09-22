package com.example.dashboardapplication;

public class Content2 {
    public String time, phone, status,duration;

    public Content2(String time, String phone, String status, String duration) {
        this.time = time;
        this.phone = phone;
        this.status = status;
        this.duration = duration;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Content2() {
    }
}
