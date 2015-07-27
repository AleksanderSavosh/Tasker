package com.aleksander.savosh.tasker.model;

import java.util.Date;

public class LogInDataBuilder {

    private LogInData logInData = new LogInData();

    public LogInDataBuilder setObjectId(String objectId) {
        logInData.objectId = objectId;
        return this;
    }

    public LogInDataBuilder setCreatedAt(Date createdAt) {
        logInData.createdAt = createdAt;
        return this;
    }

    public LogInDataBuilder setUpdatedAt(Date updatedAt) {
        logInData.updatedAt = updatedAt;
        return this;
    }

    public LogInDataBuilder setAccountId(String accountId){
        logInData.accountId = accountId;
        return this;
    }

    public LogInDataBuilder setPhoneNumber(String phoneNumber){
        logInData.phoneNumber = phoneNumber;
        return this;
    }

    public LogInDataBuilder setPassword(String password){
        logInData.password = password;
        return this;
    }

    public LogInDataBuilder setRememberMe(Boolean rememberMe){
        logInData.rememberMe = rememberMe;
        return this;
    }

    public LogInData build(){
        return new LogInData(logInData.objectId, logInData.createdAt, logInData.updatedAt,
                logInData.accountId, logInData.phoneNumber, logInData.password, logInData.rememberMe);
    }
}
