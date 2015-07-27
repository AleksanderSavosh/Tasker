package com.aleksander.savosh.tasker.model.alternative;


import com.aleksander.savosh.tasker.model.BaseModel;

import java.util.Collection;
import java.util.List;

public class Account extends BaseModel {

    public String password;
    public Collection<Phone> phones;
    public Collection<Notice> notices;

    @Override
    public String toString() {
        return "Account{" +
                "password='" + password + '\'' +
                ", phones=" + phones +
                ", notices=" + notices +
                "} " + super.toString();
    }
}
