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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;
        if (!super.equals(o)) return false;

        Property property = (Property) o;

        if (date != null ? !date.equals(property.date) : property.date != null) return false;
        if (text != null ? !text.equals(property.text) : property.text != null) return false;
        if (type != null ? !type.equals(property.type) : property.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
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
