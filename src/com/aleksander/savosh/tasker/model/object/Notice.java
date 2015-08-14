package com.aleksander.savosh.tasker.model.object;

import java.util.Date;
import java.util.List;

public class Notice extends Base {

    private List<Property> properties;

    public Notice() {}

    public Notice(List<Property> properties) {
        this.properties = properties;
    }

    public Notice(String objectId, Date createdAt, Date updatedAt, List<Property> properties) {
        super(objectId, createdAt, updatedAt);
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "properties=" + properties +
                "} " + super.toString();
    }
}
