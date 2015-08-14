package com.aleksander.savosh.tasker.model.exx;


import com.aleksander.savosh.tasker.model.BaseModel;
import com.aleksander.savosh.tasker.model.PhoneBuilder;
import com.parse.ParseClassName;

import java.util.Date;

//@ParseClassName("PhoneExx")
public class PhoneExx extends BaseModel {

    protected String number;

    protected AccountExx accountExx;

    public PhoneExx(){
//        super("PhoneExx");
    }
    public PhoneExx(String number, AccountExx accountExx) {
//        super("PhoneExx");
        this.number = number;
        this.accountExx = accountExx;
    }

    public String getNumber() {
        return number;
    }

    public AccountExx getAccountExx() {
        return accountExx;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setAccountExx(AccountExx accountExx) {
        this.accountExx = accountExx;
    }

    @Override
    public String toString() {
        return "PhoneExx{" +
                "number='" + number + '\'' +
                ", accountExx='" + accountExx.toString() + '\'' +
                "} " + super.toString();
    }
}
