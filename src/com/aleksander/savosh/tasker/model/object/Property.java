package com.aleksander.savosh.tasker.model.object;

import java.util.Date;

public class Property extends Base {

    private Integer type;
    private String text;
    private Date date;

    public Property() {}

    public Property(Integer type, String text, Date date) {
        this.type = type;
        this.text = text;
        this.date = date;
    }

    public Property(String objectId, Date createdAt, Date updatedAt, Integer type, String text, Date date) {
        super(objectId, createdAt, updatedAt);
        this.type = type;
        this.text = text;
        this.date = date;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Property{" +
                "text='" + text + '\'' +
                ", date=" + date +
                ", type=" + type +
                "} " + super.toString();
    }
}
