package com.aleksander.savosh.tasker.model.alternative;

import com.aleksander.savosh.tasker.model.BaseModel;

/**
 * Created by Alex on 27.07.2015.
 */
public class Phone extends BaseModel{

    public String number;

    @Override
    public String toString() {
        return "Phone{" +
                "number='" + number + '\'' +
                "} " + super.toString();
    }
}
