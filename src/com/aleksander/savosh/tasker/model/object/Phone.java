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
    public String toString() {
        return "Phone{" +
                "number='" + number + '\'' +
                "} " + super.toString();
    }
}
