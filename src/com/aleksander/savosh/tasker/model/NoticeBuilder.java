package com.aleksander.savosh.tasker.model;


import java.util.Date;

public class NoticeBuilder {

    private Notice notice = new Notice();

    public NoticeBuilder setObjectId(String objectId) {
        notice.objectId = objectId;
        return this;
    }

    public NoticeBuilder setCreatedAt(Date createdAt) {
        notice.createdAt = createdAt;
        return this;
    }

    public NoticeBuilder setUpdatedAt(Date updatedAt) {
        notice.updatedAt = updatedAt;
        return this;
    }

    public NoticeBuilder setAccountId(String accountId) {
        notice.accountId = accountId;
        return this;
    }

    public Notice build(){
        return new Notice(notice.objectId, notice.createdAt, notice.updatedAt, notice.accountId);
    }

}
