package com.aleksander.savosh.tasker.model.relational;


import java.util.Date;

public abstract class BaseBuilder<ChildBuilder extends BaseBuilder> {

    protected String objectId;
    protected Date createdAt;
    protected Date updatedAt;

    @SuppressWarnings("unchecked")
    public ChildBuilder setObjectId(String objectId) {
        this.objectId = objectId;
        return (ChildBuilder) this;
    }

    @SuppressWarnings("unchecked")
    public ChildBuilder setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return (ChildBuilder) this;
    }

    @SuppressWarnings("unchecked")
    public ChildBuilder setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return (ChildBuilder) this;
    }
}
