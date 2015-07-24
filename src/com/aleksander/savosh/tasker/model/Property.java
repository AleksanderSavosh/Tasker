package com.aleksander.savosh.tasker.model;

import java.util.Date;

public class Property extends BaseModel {

    protected String noticeId;
    protected PropertyType type;
    protected String text;
    protected Date date;

    Property() {
    }
    Property(String objectId, String noticeId, PropertyType type, String text, Date date) {
        super(objectId);
        this.noticeId = noticeId;
        this.type = type;
        this.text = text;
        this.date = date;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public PropertyType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    public static PropertyBuilder builder(){
        return new PropertyBuilder();
    }

    @Override
    public String toString() {
        return "Property{" +
                "noticeId='" + noticeId + '\'' +
                ", type=" + type +
                ", text='" + text + '\'' +
                ", date=" + date +
                "} " + super.toString();
    }
}
