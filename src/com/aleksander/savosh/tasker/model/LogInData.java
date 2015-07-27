package com.aleksander.savosh.tasker.model;

public class LogInData extends BaseModel {
    protected String accountId;
    protected String phoneNumber;
    protected String password;
    protected Boolean rememberMe;

    LogInData() {
    }

    public LogInData(String objectId, String accountId, String phoneNumber, String password) {
        super(objectId);
        this.accountId = accountId;
        this.phoneNumber = phoneNumber;
        this.password = password;
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

    public static LogInDataBuilder builder(){
        return new LogInDataBuilder();
    }

    @Override
    public String toString() {
        return "LogInData{" +
                "accountId='" + accountId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }
}
