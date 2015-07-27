package com.aleksander.savosh.tasker.model;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseModel implements Serializable {

    protected String objectId;
    protected Date createdAt;
    protected Date updatedAt;

    protected BaseModel() {}
    protected BaseModel(String objectId, Date createdAt, Date updatedAt) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "objectId='" + objectId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
