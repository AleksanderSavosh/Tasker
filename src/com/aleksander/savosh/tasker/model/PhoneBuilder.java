package com.aleksander.savosh.tasker.model;

import java.util.Date;

public class PhoneBuilder {

    private Phone phone = new Phone();

    PhoneBuilder() {}

    public PhoneBuilder setObjectId(String objectId) {
        phone.objectId = objectId;
        return this;
    }

    public PhoneBuilder setCreatedAt(Date createdAt) {
        phone.createdAt = createdAt;
        return this;
    }

    public PhoneBuilder setUpdatedAt(Date updatedAt) {
        phone.updatedAt = updatedAt;
        return this;
    }

    public PhoneBuilder setNumber(String number){
        phone.number = number;
        return this;
    }

    public PhoneBuilder setAccountId(String accountId){
        phone.accountId = accountId;
        return this;
    }

    public Phone build(){
        return new Phone(phone.objectId, phone.createdAt, phone.updatedAt, phone.number, phone.accountId);
    }
}
