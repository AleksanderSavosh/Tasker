package com.aleksander.savosh.tasker.model;

/**
 * Created by Alex on 10.07.2015.
 */
public class PhoneBuilder extends Phone {

    public PhoneBuilder addObjectId(String objectId){
        setObjectId(objectId);
        return this;
    }

    public PhoneBuilder addNumber(String number){
        setNumber(number);
        return this;
    }

    public PhoneBuilder addAccountId(String accountId){
        setAccountId(accountId);
        return this;
    }

    public Phone build(){
        return this;
    }
}
