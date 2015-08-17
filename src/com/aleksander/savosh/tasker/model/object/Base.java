package com.aleksander.savosh.tasker.model.object;

import java.io.Serializable;
import java.util.Date;

public abstract class Base implements Serializable {

    private String objectId;
    private Date createdAt;
    private Date updatedAt;

    public Base() {
    }

    public Base(String objectId, Date createdAt, Date updatedAt) {
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Base)) return false;

        Base base = (Base) o;

        if (createdAt != null ? !createdAt.equals(base.createdAt) : base.createdAt != null) return false;
        if (objectId != null ? !objectId.equals(base.objectId) : base.objectId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = objectId != null ? objectId.hashCode() : 0;
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Base{" +
                "objectId='" + objectId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
