package com.aleksander.savosh.tasker.model;


public class NoticeBuilder extends BaseBuilder<AccountBuilder> {

    private Notice notice = new Notice();

    public NoticeBuilder setAccountId(String accountId) {
        notice.accountId = accountId;
        return this;
    }

    public Notice build(){
        return new Notice(objectId, createdAt, updatedAt, notice.accountId);
    }

}
