package com.aleksander.savosh.tasker.model;

import java.util.Date;

public class Notice extends BaseModel {

    protected String accountId;

    Notice() {}
    Notice(String objectId, Date createdAt, Date updatedAt, String accountId) {
        super(objectId, createdAt, updatedAt);
        this.accountId = accountId;
    }

    public static NoticeBuilder builder(){
        return new NoticeBuilder();
    }

    @Override
    public String toString() {
        return "Notice{" +
                "accountId='" + accountId + '\'' +
                "} " + super.toString();
    }
}
