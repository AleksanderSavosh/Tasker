package com.aleksander.savosh.tasker.model;

public class LogInDataBuilder {

    private LogInData logInData = new LogInData();

    public LogInDataBuilder addObjectId(String objectId){
        logInData.objectId = objectId;
        return this;
    }

    public LogInDataBuilder addAccountId(String accountId){
        logInData.accountId = accountId;
        return this;
    }

    public LogInDataBuilder addPhoneNumber(String phoneNumber){
        logInData.phoneNumber = phoneNumber;
        return this;
    }

    public LogInDataBuilder addPassword(String password){
        logInData.password = password;
        return this;
    }

    public LogInData build(){
        return new LogInData(logInData.objectId, logInData.accountId, logInData.phoneNumber, logInData.password);
    }
}
