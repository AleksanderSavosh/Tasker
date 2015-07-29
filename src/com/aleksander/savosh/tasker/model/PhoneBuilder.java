package com.aleksander.savosh.tasker.model;


public class PhoneBuilder extends BaseBuilder<AccountBuilder> {

    private Phone phone = new Phone();

    PhoneBuilder() {}

    public PhoneBuilder setNumber(String number){
        phone.number = number;
        return this;
    }

    public PhoneBuilder setAccountId(String accountId){
        phone.accountId = accountId;
        return this;
    }

    public Phone build(){
        return new Phone(objectId, createdAt, updatedAt, phone.number, phone.accountId);
    }
}
