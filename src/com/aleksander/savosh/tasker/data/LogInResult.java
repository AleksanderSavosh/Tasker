package com.aleksander.savosh.tasker.data;


public class LogInResult {
    public Boolean isLogIn = false;
    public String message = "";

    @Override
    public String toString() {
        return "LogInResult{" +
                "isLogIn=" + isLogIn +
                ", message='" + message + '\'' +
                '}';
    }
}
