package com.aleksander.savosh.tasker.model;

import java.util.Date;

public class AccountBuilder {

    private Account account = new Account();

    AccountBuilder(){}

    public AccountBuilder setObjectId(String objectId) {
        account.objectId = objectId;
        return this;
    }

    public AccountBuilder setCreatedAt(Date createdAt) {
        account.createdAt = createdAt;
        return this;
    }

    public AccountBuilder setUpdatedAt(Date updatedAt) {
        account.updatedAt = updatedAt;
        return this;
    }

    public AccountBuilder addPassword(String password){
        account.password = password;
        return this;
    }

    public Account build(){
        return new Account(account.objectId, account.createdAt, account.updatedAt, account.password);
    }
}
