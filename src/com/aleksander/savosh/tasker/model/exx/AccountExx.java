package com.aleksander.savosh.tasker.model.exx;

import com.aleksander.savosh.tasker.model.relational.BaseModel;

import java.util.Date;

//@ParseClassName("AccountExx")
public class AccountExx extends BaseModel {

    protected String password;
    public AccountExx(){
//        super("AccountExx");
    }
    public AccountExx(String password) {
//        super("AccountExx");
        this.password = password;
    }

    public AccountExx(String objectId, Date createdAt, Date updatedAt, String password) {
        super(objectId, createdAt, updatedAt);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AccountExx{" +
                "password='" + password + '\'' +
                "} " + super.toString();
    }
}
