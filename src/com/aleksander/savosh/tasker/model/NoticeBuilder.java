package com.aleksander.savosh.tasker.model;


public class NoticeBuilder {

    private Notice notice = new Notice();

    public NoticeBuilder setAccountId(String accountId) {
        notice.accountId = accountId;
        return this;
    }

    public Notice build(){
        return new Notice(notice.objectId, notice.accountId);
    }

}
