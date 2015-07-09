package com.aleksander.savosh.tasker.model;

public class Account extends BaseModel {

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Account{" +
                "password='" + password + '\'' +
                '}';
    }
}
