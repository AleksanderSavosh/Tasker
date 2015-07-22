package com.aleksander.savosh.tasker.model;

public class PhoneBuilder {

    private Phone phone = new Phone();

    PhoneBuilder() {}

    public PhoneBuilder addObjectId(String objectId){
        phone.objectId = objectId;
        return this;
    }

    public PhoneBuilder addNumber(String number){
        phone.number = number;
        return this;
    }

    public PhoneBuilder addAccountId(String accountId){
        phone.accountId = accountId;
        return this;
    }

    public Phone build(){
        return new Phone(phone.objectId, phone.number, phone.accountId);
    }
}
