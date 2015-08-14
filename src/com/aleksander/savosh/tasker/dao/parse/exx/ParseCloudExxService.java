package com.aleksander.savosh.tasker.dao.parse.exx;

import com.aleksander.savosh.tasker.model.exx.AccountExx;
import com.aleksander.savosh.tasker.model.exx.PhoneExx;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseCloudExxService {


    public static AccountExx createAccountExx(AccountExx accountExx) throws ParseException {

        ParseObject object = ParseObject.create(accountExx.getClass().getSimpleName());
        object.put("Password", accountExx.getPassword());
        object.save();

        return new AccountExx(
                object.getString("Password"));
    }


    public static PhoneExx createPhoneExx(PhoneExx phoneExx) throws ParseException {

        ParseObject object = ParseObject.create(phoneExx.getClass().getSimpleName());
        object.put("Number", phoneExx.getNumber());
        object.put("AccountExx", ParseQuery.getQuery("AccountExx").get(phoneExx.getAccountExx().getObjectId()));
        object.save();

        return new PhoneExx(
                object.getString("Number"),
                (AccountExx) object.get("AccountExx"));
    }


}
