package com.aleksander.savosh.tasker.service;


import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.dao.LocalDao;
import com.aleksander.savosh.tasker.model.Notice;
import com.aleksander.savosh.tasker.model.NoticeWithProperties;

import java.util.ArrayList;
import java.util.List;

public class NoticeService {

    public static final String DEFAULT_ACCOUNT_ID = "noAccountId";

    public static List<NoticeWithProperties> getLocalNoticesByAccountId(String accountId){
        try {
            LocalDao<Notice> noticeLocalDao = Application.getNoticeLocalDao();
            List<Notice> notices = noticeLocalDao.readThrowExceptions(Notice.builder().setAccountId(accountId).build());
            List<NoticeWithProperties> list = new ArrayList<NoticeWithProperties>();
            for(Notice notice : notices){
                list.add(new NoticeWithProperties(notice, PropertyService.getLocalNoticeProperties(notice)));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<NoticeWithProperties>();
    }

    public static List<NoticeWithProperties> getLocalNoticesWithoutAccountId(){
        try {
            LocalDao<Notice> noticeLocalDao = Application.getNoticeLocalDao();
            List<Notice> notices = noticeLocalDao.readThrowExceptions(Notice.builder()
                    .setAccountId(DEFAULT_ACCOUNT_ID)
                    .build());
            List<NoticeWithProperties> list = new ArrayList<NoticeWithProperties>();
            for(Notice notice : notices){
                list.add(new NoticeWithProperties(notice, PropertyService.getLocalNoticeProperties(notice)));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<NoticeWithProperties>();
    }
}
