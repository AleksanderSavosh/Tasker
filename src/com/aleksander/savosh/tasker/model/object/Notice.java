package com.aleksander.savosh.tasker.model.object;

import java.util.Date;
import java.util.List;

public class Notice extends Base {

    private List<Property> properties;

    public Notice() {}

    public Notice(List<Property> properties) {
        this.properties = properties;
    }

    public Notice(String objectId, Date createdAt, Date updatedAt, List<Property> properties) {
        super(objectId, createdAt, updatedAt);
        this.properties = properties;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notice)) return false;
        if (!super.equals(o)) return false;

        Notice notice = (Notice) o;

        if (properties != null ? !properties.equals(notice.properties) : notice.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "properties=" + properties +
                "} " + super.toString();
    }
}
