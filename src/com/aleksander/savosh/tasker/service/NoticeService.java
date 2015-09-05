package com.aleksander.savosh.tasker.service;


import android.util.Log;
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

//    public static final String DEFAULT_ACCOUNT_ID = "noAccountId";

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

        if (StringUtil.isEmpty(config.accountId)) { //create local notice
            Log.d(NoticeService.class.getName(), "Create local notice");
//            Account zero = application.getAccounts().get(Config.ACC_ZERO);
            Account zero = Application.getAccountLocalDao().readWithRelations(Config.ACC_ZERO);
            if(zero == null){
                Log.d(NoticeService.class.getName(), "Create first local notice");
                zero = Application.getAccountLocalDao()
                        .createWithRelationsThrowException(
                                new Account(Config.ACC_ZERO, null, null, null, null, new ArrayList<Notice>(Arrays.asList(notice))));
//                Log.i("ACCOUNT ZERO", "BEFORE ID: " + zero.getObjectId());
//                Application.getAccountLocalDao().createWithRelationsThrowException(zero);
//                Log.i("ACCOUNT ZERO", "AFTER  ID: " + zero.getObjectId());
//                Log.i("ACCOUNT ZERO", "ZERO ACC: " + Application.getAccountLocalDao().readWithRelations(Config.ACC_ZERO));

                application.getAccounts().put(Config.ACC_ZERO, zero);
            } else {
                zero.getNotices().add(notice);
                zero = Application.getAccountLocalDao().updateWithRelationsThrowException(zero);
                application.getAccounts().remove(Config.ACC_ZERO);
                application.getAccounts().put(Config.ACC_ZERO, zero);
            }

        } else { //create cloud notice
            Log.d(NoticeService.class.getName(), "Create cloud notice");
            Account acc = application.getAccounts().get(config.accountId);
            acc.getNotices().add(notice);
            Application.getAccountCloudDao().updateWithRelationsThrowException(acc);
            Application.getAccountLocalDao().updateWithRelationsThrowException(acc);

        }
    }

    public static void updateNotice(Notice notice) throws OtherException, DataNotFoundException {
        Application application = (Application) Application.getContext().getApplicationContext();
        Config config = application.getConfig();

        String accountId = StringUtil.isEmpty(config.accountId) ? Config.ACC_ZERO : config.accountId;

        //обновляем заметку в памяти
        Account acc = application.getAccounts().get(accountId);
        List<Notice> notices = acc.getNotices();
        Iterator<Notice> iterator = notices.iterator();
        while (iterator.hasNext()){
            if(iterator.next().getObjectId().equals(notice.getObjectId())){
                iterator.remove();
                break;
            }
        }
        notices.add(notice);

        if(!StringUtil.isEmpty(config.accountId)) {
            //обновляем заметку в облаке
            Application.getNoticeCloudDao().updateWithRelationsThrowException(notice);
        }

        //обновляем заметку в локальном хранилище
        Application.getNoticeLocalDao().updateWithRelationsThrowException(notice);

    }
}
