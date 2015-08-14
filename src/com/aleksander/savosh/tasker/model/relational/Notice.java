package com.aleksander.savosh.tasker.model.relational;

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

    public static NoticeBuilder builder(Notice notice){
        return new NoticeBuilder(notice);
    }

    @Override
    public String toString() {
        return "Notice{" +
                "accountId='" + accountId + '\'' +
                "} " + super.toString();
    }
}
