package com.aleksander.savosh.tasker.model.alternative;

import com.aleksander.savosh.tasker.model.BaseModel;

import java.util.Collection;
import java.util.List;

public class Notice extends BaseModel {

    public Collection<Property> properties;

    @Override
    public String toString() {
        return "Notice{" +
                "properties=" + properties +
                "} " + super.toString();
    }
}
