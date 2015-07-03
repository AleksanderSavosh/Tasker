package com.aleksander.savosh.tasker.model;

import com.parse.ParseObject;

public class Phone {

    private String id;
    private String number;
    private String accountId;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public static Phone fromParseObject(ParseObject object){
        Phone phone = new Phone();
        phone.id = object.getObjectId();
        phone.number = object.getString("number");
        phone.accountId = object.getString("accountId");
        return phone;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "accountId='" + accountId + '\'' +
                ", number='" + number + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
