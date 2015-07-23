package com.aleksander.savosh.tasker.model;

import java.util.List;
import java.util.Map;

public class Notice extends BaseModel {

    protected String accountId;
    protected Map<PropertyType, List<Property>> properties;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Map<PropertyType, List<Property>> getProperties() {
        return properties;
    }

    public void setProperties(Map<PropertyType, List<Property>> properties) {
        this.properties = properties;
    }
}
