package com.aleksander.savosh.tasker.model;

import java.util.List;
import java.util.Map;

public class Notice extends BaseModel {

    protected String accountId;
    protected Map<PropertyType, List<Property>> properties;

    public String getAccountId() {
        return accountId;
    }

    public Map<PropertyType, List<Property>> getProperties() {
        return properties;
    }
}
