package com.aleksander.savosh.tasker.model;

public class Notice extends BaseModel {


    protected String accountId;

    Notice() {}
    Notice(String objectId, String accountId) {
        super(objectId);
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
