package com.aleksander.savosh.tasker.data;


public class LogInData {
    public String number;
    public String password;
    public Boolean rememberMe;

    public LogInData() {
    }

    public LogInData(String number, String password, Boolean rememberMe) {
        this.number = number;
        this.password = password;
        this.rememberMe = rememberMe;
    }
}