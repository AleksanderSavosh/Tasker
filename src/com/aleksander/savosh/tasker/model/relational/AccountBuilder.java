package com.aleksander.savosh.tasker.model.relational;


public class AccountBuilder extends BaseBuilder<AccountBuilder> {

    private Account account = new Account();

    AccountBuilder(){}

    public AccountBuilder setPassword(String password){
        account.password = password;
        return this;
    }

    public Account build(){
        return new Account(objectId, createdAt, updatedAt, account.password);
    }
}
