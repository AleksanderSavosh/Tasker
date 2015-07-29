package com.aleksander.savosh.tasker.model;


public class LogInDataBuilder extends BaseBuilder<AccountBuilder> {

    private LogInData logInData = new LogInData();

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
        return new LogInData(objectId, createdAt, updatedAt, logInData.accountId, logInData.phoneNumber,
                logInData.password, logInData.rememberMe);
    }
}
