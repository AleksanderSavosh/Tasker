package com.aleksander.savosh.tasker.model;

import java.util.Date;

public class PropertyBuilder {

    private Property property = new Property();

    public PropertyBuilder setObjectId(String objectId) {
        property.objectId = objectId;
        return this;
    }

    public PropertyBuilder setCreatedAt(Date createdAt) {
        property.createdAt = createdAt;
        return this;
    }

    public PropertyBuilder setUpdatedAt(Date updatedAt) {
        property.updatedAt = updatedAt;
        return this;
    }

    public PropertyBuilder setNoticeId(String noticeId) {
        property.noticeId = noticeId;
        return this;
    }

    public PropertyBuilder setType(Integer type) {
        property.type = type;
        return this;
    }

    public PropertyBuilder setText(String text) {
        property.text = text;
        return this;
    }

    public PropertyBuilder setDate(Date date) {
        property.date = date;
        return this;
    }

    public Property build(){
        return new Property(property.objectId, property.createdAt, property.updatedAt, property.noticeId,
                property.type, property.text, property.date);
    }
}
