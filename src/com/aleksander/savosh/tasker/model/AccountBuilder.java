package com.aleksander.savosh.tasker.model;

/**
 * Created by Alex on 09.07.2015.
 */
public class AccountBuilder extends Account {

    public AccountBuilder addObjectId(String objectId){
        setObjectId(objectId);
        return this;
    }

    private AccountBuilder addPassword(String password){
        setPassword(password);
        return this;
    }

    public Account build(){
        return this;
    }
}
