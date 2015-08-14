package com.aleksander.savosh.tasker.model.object;

import java.util.Date;
import java.util.List;

public class Account extends Base {

    private String password;
    private List<Phone> phones;
    private List<Notice> notices;

    public Account() {}

    public Account(String password, List<Phone> phones, List<Notice> notices) {
        this.password = password;
        this.phones = phones;
        this.notices = notices;
    }

    public Account(String objectId, Date createdAt, Date updatedAt, String password, List<Phone> phones, List<Notice> notices) {
        super(objectId, createdAt, updatedAt);
        this.password = password;
        this.phones = phones;
        this.notices = notices;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

    @Override
    public String toString() {
        return "Account{" +
                "password='" + password + '\'' +
                ", phones=" + phones +
                ", notices=" + notices +
                "} " + super.toString();
    }
}
