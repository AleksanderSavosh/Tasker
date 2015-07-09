package com.aleksander.savosh.tasker.service;


import com.aleksander.savosh.tasker.model.Account;
import com.aleksander.savosh.tasker.model.Phone;
import com.parse.ParseObject;

public class ParseModelFactory {

    public static Phone createPhone(ParseObject object){
        Phone phone = new Phone();
        phone.setObjectId(object.getObjectId());
        phone.setNumber(object.getString("number"));
        phone.setAccountId(object.getString("accountId"));
        return phone;
    }


    public static Account createAccount(ParseObject object) {
        Account account = new Account();
        account.setObjectId(object.getObjectId());
        account.setPassword(object.getString("password"));
        return account;
    }
}
