package com.aleksander.savosh.tasker.model.object;

import java.util.Date;

public class Phone extends Base {

    private String number;

    public Phone() {}

    public Phone(String number) {
        this.number = number;
    }

    public Phone(String objectId, Date createdAt, Date updatedAt, String number) {
        super(objectId, createdAt, updatedAt);
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Phone)) return false;
        if (!super.equals(o)) return false;

        Phone phone = (Phone) o;

        if (number != null ? !number.equals(phone.number) : phone.number != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (number != null ? number.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "number='" + number + '\'' +
                "} " + super.toString();
    }
}
