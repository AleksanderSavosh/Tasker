package com.aleksander.savosh.tasker.model;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseModel implements Serializable {

    protected String objectId;
    protected Date createdAt;
    protected Date updatedAt;

    BaseModel() {}
    BaseModel(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "objectId='" + objectId + '\'' +
                '}';
    }
}
