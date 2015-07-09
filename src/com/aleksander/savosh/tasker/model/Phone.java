package com.aleksander.savosh.tasker.model;


public class Phone extends BaseModel {

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


}
