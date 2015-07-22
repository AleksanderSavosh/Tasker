package com.aleksander.savosh.tasker.model;

public abstract class BaseModel {

    protected String objectId;

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
