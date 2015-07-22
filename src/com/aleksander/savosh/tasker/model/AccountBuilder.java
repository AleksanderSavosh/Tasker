package com.aleksander.savosh.tasker.model;

public class AccountBuilder {

    private Account account = new Account();

    AccountBuilder(){}

    public AccountBuilder addObjectId(String objectId){
        account.objectId = objectId;
        return this;
    }

    public AccountBuilder addPassword(String password){
        account.password = password;
        return this;
    }

    public Account build(){
        return new Account(account.objectId, account.password);
    }
}
