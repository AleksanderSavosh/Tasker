package com.aleksander.savosh.tasker.model.relational;

import java.util.Date;

public class Account extends BaseModel {

    protected String password;
    Account(){}
    Account(String objectId, Date createdAt, Date updatedAt, String password) {
        super(objectId, createdAt, updatedAt);
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
