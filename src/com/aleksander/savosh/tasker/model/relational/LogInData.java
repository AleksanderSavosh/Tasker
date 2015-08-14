package com.aleksander.savosh.tasker.model.relational;

import java.util.Date;

public class LogInData extends BaseModel {
    protected String accountId;
    protected String phoneNumber;
    protected String password;
    protected Boolean rememberMe;

    LogInData() {
    }

    public LogInData(String objectId, Date createdAt, Date updatedAt, String accountId, String phoneNumber, String password, Boolean rememberMe) {
        super(objectId, createdAt, updatedAt);
        this.accountId = accountId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public static LogInDataBuilder builder(){
        return new LogInDataBuilder();
    }

    @Override
    public String toString() {
        return "LogInData{" +
                "accountId='" + accountId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", rememberMe=" + rememberMe +
                "} " + super.toString();
    }
}
