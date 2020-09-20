package com.example.dashboardapplication;

public class Content {
    public String name, date, time, phone, status,firstcall;

    public Content(String name, String date, String time, String phone, String status, String firstcall) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.phone = phone;
        this.status = status;
        this.firstcall = firstcall;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFirstcall() {
        return firstcall;
    }

    public void setFirstcall(String firstcall) {
        this.firstcall = firstcall;
    }

    public Content() {
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

}
