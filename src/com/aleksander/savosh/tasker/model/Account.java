package com.aleksander.savosh.tasker.model;

public class Account extends BaseModel {

    protected String password;
    Account(){}
    Account(String objectId, String password) {
        super(objectId);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public static AccountBuilder builder(){
        return new AccountBuilder();
    }

    @Override
    public String toString() {
        return "Account{" +
                "password='" + password + '\'' +
                "} " + super.toString();
    }
}
