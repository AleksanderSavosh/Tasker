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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;

        Account account = (Account) o;

        if (notices != null ? !notices.equals(account.notices) : account.notices != null) return false;
        if (password != null ? !password.equals(account.password) : account.password != null) return false;
        if (phones != null ? !phones.equals(account.phones) : account.phones != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = password != null ? password.hashCode() : 0;
        result = 31 * result + (phones != null ? phones.hashCode() : 0);
        result = 31 * result + (notices != null ? notices.hashCode() : 0);
        return result;
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
