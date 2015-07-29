package com.aleksander.savosh.tasker.model;

import java.util.Date;

public class PropertyBuilder extends BaseBuilder<AccountBuilder> {

    private Property property = new Property();

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
        return new Property(objectId, createdAt, updatedAt, property.noticeId,
                property.type, property.text, property.date);
    }
}
