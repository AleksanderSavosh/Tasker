package com.aleksander.savosh.tasker.model.alternative;

import com.aleksander.savosh.tasker.model.BaseModel;
import com.aleksander.savosh.tasker.model.PropertyBuilder;

import java.util.Date;

public class Property extends BaseModel {

    public Integer type;
    public String text;
    public Date date;

    @Override
    public String toString() {
        return "Property{" +
                "type=" + type +
                ", text='" + text + '\'' +
                ", date=" + date +
                "} " + super.toString();
    }
}
