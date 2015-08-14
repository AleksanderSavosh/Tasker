package com.aleksander.savosh.tasker.model.relational;


public class PhoneBuilder extends BaseBuilder<PhoneBuilder> {

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
