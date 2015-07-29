package com.aleksander.savosh.tasker.model;


public class NoticeBuilder extends BaseBuilder<NoticeBuilder> {

    private Notice notice = new Notice();

    NoticeBuilder() {}
    NoticeBuilder(Notice notice) {
        this.notice = new Notice(notice.objectId, notice.createdAt, notice.updatedAt, notice.accountId);
    }

    public NoticeBuilder setAccountId(String accountId) {
        notice.accountId = accountId;
        return this;
    }

    public Notice build(){
        return new Notice(objectId, createdAt, updatedAt, notice.accountId);
    }

}
