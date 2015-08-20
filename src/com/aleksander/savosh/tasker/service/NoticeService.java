package com.aleksander.savosh.tasker.service;


import android.widget.Toast;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.StringUtil;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import java.util.*;

public class NoticeService {

    public static final String DEFAULT_ACCOUNT_ID = "noAccountId";

//    public static List<NoticeWithProperties> getLocalNoticesByAccountId(String accountId){
//        try {
//            LocalDao<Notice> noticeLocalDao = Application.getNoticeLocalDao();
//            List<Notice> notices = noticeLocalDao.readThrowExceptions(Notice.builder().setAccountId(accountId).build());
//            List<NoticeWithProperties> list = new ArrayList<NoticeWithProperties>();
//            for(Notice notice : notices){
//                list.add(new NoticeWithProperties(notice, PropertyService.getLocalNoticeProperties(notice)));
//            }
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<NoticeWithProperties>();
//    }
//
//    public static List<NoticeWithProperties> getLocalNoticesWithoutAccountId(){
//        try {
//            LocalDao<Notice> noticeLocalDao = Application.getNoticeLocalDao();
//            List<Notice> notices = noticeLocalDao.readThrowExceptions(Notice.builder()
//                    .setAccountId(DEFAULT_ACCOUNT_ID)
//                    .build());
//            List<NoticeWithProperties> list = new ArrayList<NoticeWithProperties>();
//            for(Notice notice : notices){
//                list.add(new NoticeWithProperties(notice, PropertyService.getLocalNoticeProperties(notice)));
//            }
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<NoticeWithProperties>();
//    }

    public static void createNotice(Map<Integer, List<Property>> propertiesMap) throws OtherException,
            CannotCreateException, DataNotFoundException {

        List<Property> properties = new ArrayList<Property>();
        for(Integer key : propertiesMap.keySet()){
            properties.addAll(propertiesMap.get(key));
        }


        Application application = (Application) Application.getContext().getApplicationContext();
        Config config = application.getConfig();
        Notice notice = new Notice(properties);

        if (StringUtil.isEmpty(config.rememberMeAccountId)) { //create local notice


            Application.getNoticeLocalDao().createWithRelationsThrowException(notice);
            application.getLocalNotices().put(notice.getObjectId(), notice);


        } else { //create cloud notice

            Account acc = application.getAccounts().get(config.rememberMeAccountId);
            acc.getNotices().add(notice);
            Application.getAccountCloudDao().updateWithRelationsThrowException(acc);
            Application.getAccountLocalDao().updateWithRelationsThrowException(acc);


        }
    }

}
