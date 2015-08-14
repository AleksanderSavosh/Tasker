package com.aleksander.savosh.tasker.dao.relational.parse.exx;

import com.aleksander.savosh.tasker.model.exx.AccountExx;
import com.aleksander.savosh.tasker.model.exx.PhoneExx;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseCloudExxService {


    public static AccountExx createAccountExx(AccountExx accountExx) throws ParseException {

        ParseObject object = ParseObject.create(accountExx.getClass().getSimpleName());
        object.put("Password", accountExx.getPassword());
        object.save();

        return new AccountExx(
                object.getObjectId(),
                object.getCreatedAt(),
                object.getUpdatedAt(),
                object.getString("Password"));
    }


    public static PhoneExx createPhoneExx(PhoneExx phoneExx) throws ParseException {

        ParseObject object = ParseObject.create(phoneExx.getClass().getSimpleName());
        object.put("Number", phoneExx.getNumber());
        object.put("AccountExx", ParseQuery.getQuery("AccountExx").get(phoneExx.getAccountExx().getObjectId()));
        object.save();


        ParseObject objectAccExx = object.getParseObject("AccountExx");
        AccountExx accountExx = new AccountExx(
                objectAccExx.getObjectId(),
                objectAccExx.getCreatedAt(),
                objectAccExx.getUpdatedAt(),
                objectAccExx.getString("Password"));


        return new PhoneExx(
                object.getObjectId(),
                object.getCreatedAt(),
                object.getUpdatedAt(),
                object.getString("Number"),
                accountExx);
    }



    public static PhoneExx createPhoneExxWithAccountExx(PhoneExx phoneExx) throws ParseException {

        ParseObject objectAcc = ParseObject.create(phoneExx.getAccountExx().getClass().getSimpleName());
        objectAcc.put("Password", phoneExx.getAccountExx().getPassword());

        ParseObject object = ParseObject.create(phoneExx.getClass().getSimpleName());
        object.put("Number", phoneExx.getNumber());
        object.put("AccountExx", objectAcc);


        object.save();


        AccountExx accountExx = new AccountExx(
                objectAcc.getObjectId(),
                objectAcc.getCreatedAt(),
                objectAcc.getUpdatedAt(),
                objectAcc.getString("Password"));


        return new PhoneExx(
                object.getObjectId(),
                object.getCreatedAt(),
                object.getUpdatedAt(),
                object.getString("Number"),
                accountExx);
    }

}
