package com.aleksander.savosh.tasker.model;

import java.util.Date;

public class Property extends BaseModel {

    protected String noticeId;
    protected PropertyType type;
    protected String text;
    protected Date date;

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
}
