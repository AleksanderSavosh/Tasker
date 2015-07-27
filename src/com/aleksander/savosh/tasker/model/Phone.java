package com.aleksander.savosh.tasker.model;


import java.util.Date;

public class Phone extends BaseModel {

    protected String number;
    protected String accountId;

    Phone(){}
    Phone(String objectId, Date createdAt, Date updatedAt, String number, String accountId) {
        super(objectId, createdAt, updatedAt);
        this.number = number;
        this.accountId = accountId;
    }

    public String getNumber() {
        return number;
    }

    public String getAccountId() {
        return accountId;
    }

    public static PhoneBuilder builder(){
        return new PhoneBuilder();
    }

    @Override
    public String toString() {
        return "Phone{" +
                "number='" + number + '\'' +
                ", accountId='" + accountId + '\'' +
                "} " + super.toString();
    }
}
