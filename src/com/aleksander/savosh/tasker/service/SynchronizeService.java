package com.aleksander.savosh.tasker.service;

import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.model.object.Account;


public class SynchronizeService {


//    public static void transferLocalNoticesToCloud(String accountId) throws Exception {
//        LocalDao<Notice> noticeLocalDao = Application.getNoticeLocalDao();
//        CloudDao<Notice> noticeCloudDao = Application.getNoticeCloudDao();
//        LocalDao<Property> propertyLocalDao = Application.getPropertyLocalDao();
//        CloudDao<Property> propertyCloudDao = Application.getPropertyCloudDao();
//
//        List<Notice> updatedLocalNoticeList = new ArrayList<Notice>();
//        List<Notice> createdCloudNoticeList = new ArrayList<Notice>();
//        List<Property> updatedLocalPropertyList = new ArrayList<Property>();
//        List<Property> createdCloudPropertyList = new ArrayList<Property>();
//
//        try {
//            List<Notice> localNoticeList = noticeLocalDao.read(Notice.builder()
//                    .setAccountId(NoticeService.DEFAULT_ACCOUNT_ID)
//                    .build());
//
//            for (Notice localNotice : localNoticeList) {
//                Notice cloudNotice = noticeCloudDao.createThrowExceptions(Notice.builder(localNotice)
//                        .setAccountId(accountId)
//                        .build());
//                createdCloudNoticeList.add(cloudNotice);
//                noticeLocalDao.deleteThrowExceptions(localNotice);
//                noticeLocalDao.createThrowExceptions(cloudNotice);
//                updatedLocalNoticeList.add(localNotice);
//
//                List<Property> localPropertyList = propertyLocalDao.readThrowExceptions(Property.builder()
//                        .setNoticeId(localNotice.getObjectId())
//                        .build());
//
//                for (Property localProperty : localPropertyList) {
//                    Property cloudProperty = propertyCloudDao.createThrowExceptions(Property
//                            .builder(localProperty).setNoticeId(cloudNotice.getObjectId()).build());
//                    createdCloudPropertyList.add(cloudProperty);
//                    propertyLocalDao.deleteThrowExceptions(localProperty);
//                    propertyLocalDao.createThrowExceptions(cloudProperty);
//                    updatedLocalPropertyList.add(localProperty);
//                }
//            }
//        } catch(Exception e){
//            Log.d(SynchronizeService.class.getName(), "#################### ROLLBACK ####################");
//            Log.d(SynchronizeService.class.getName(), ".  updatedLocalNoticeList: " + updatedLocalNoticeList);
//            Log.d(SynchronizeService.class.getName(), ".  createdCloudNoticeList: " + createdCloudNoticeList);
//            Log.d(SynchronizeService.class.getName(), ".updatedLocalPropertyList: " + updatedLocalPropertyList);
//            Log.d(SynchronizeService.class.getName(), ".createdCloudPropertyList: " + createdCloudPropertyList);
//
//            for (Notice notice : updatedLocalNoticeList) {
//                noticeLocalDao.create(notice);
//            }
//
//            for (Notice notice : createdCloudNoticeList) {
//                noticeCloudDao.delete(notice);
//            }
//
//            for (Property property : updatedLocalPropertyList) {
//                propertyLocalDao.create(property);
//            }
//            for (Property property : createdCloudPropertyList) {
//                propertyCloudDao.delete(property);
//            }
//
//            Log.d(SynchronizeService.class.getName(), "##################################################");
//            throw e;
//        }
//    }
//
//    public static void updateLocalByCloud(String accountId) throws Exception {
//        LocalDao<Notice> noticeLocalDao = Application.getNoticeLocalDao();
//        CloudDao<Notice> noticeCloudDao = Application.getNoticeCloudDao();
//        LocalDao<Property> propertyLocalDao = Application.getPropertyLocalDao();
//        CloudDao<Property> propertyCloudDao = Application.getPropertyCloudDao();
//
//        Map<String, Notice> localNoticeMap;
//        Map<String, Notice> cloudNoticeMap;
//
//        List<Notice> createdLocalNoticeList = new ArrayList<Notice>();
//        List<Property> createdLocalPropertyList = new ArrayList<Property>();
//        List<Property> deletedLocalPropertyList = new ArrayList<Property>();
//
//        try {
//            localNoticeMap = getMapOfObjectIdsAndObject(noticeLocalDao
//                    .readThrowExceptions(Notice.builder().setAccountId(accountId).build()));
//            cloudNoticeMap = getMapOfObjectIdsAndObject(noticeCloudDao
//                    .readThrowExceptions(Notice.builder().setAccountId(accountId).build()));
//
//
//            for(String cloudNoticeId : cloudNoticeMap.keySet()){
//                if(localNoticeMap.containsKey(cloudNoticeId)){
//                    //update local
//                    Notice forUpdate = cloudNoticeMap.get(cloudNoticeId);
//                    List<Property> forDeleteList = propertyLocalDao
//                            .readThrowExceptions(Property.builder().setNoticeId(forUpdate.getObjectId()).build());
//                    for(Property forDelete : forDeleteList){
//                        propertyLocalDao.delete(forDelete);
//                        deletedLocalPropertyList.add(forDelete);
//                    }
//
//                    List<Property> forCreateList = propertyCloudDao
//                            .readThrowExceptions(Property.builder().setNoticeId(forUpdate.getObjectId()).build());
//                    for(Property forCreate : forCreateList){
//                        propertyLocalDao.createThrowExceptions(forCreate);
//                        createdLocalPropertyList.add(forCreate);
//                    }
//                } else {
//                    //create local
//                    Notice forCreate = cloudNoticeMap.get(cloudNoticeId);
//                    noticeLocalDao.createThrowExceptions(forCreate);
//                    createdLocalNoticeList.add(forCreate);
//
//                    List<Property> forCreateList = propertyCloudDao
//                            .readThrowExceptions(Property.builder().setNoticeId(forCreate.getObjectId()).build());
//                    for(Property property : forCreateList){
//                        propertyLocalDao.createThrowExceptions(property);
//                        createdLocalPropertyList.add(property);
//                    }
//                }
//            }
//
//        } catch(Exception e){
//            Log.d(SynchronizeService.class.getName(), "#################### ROLLBACK ####################");
//            Log.d(SynchronizeService.class.getName(), ".    createdLocalNoticeList: " + createdLocalNoticeList);
//            Log.d(SynchronizeService.class.getName(), ".  createdLocalPropertyList: " + createdLocalPropertyList);
//            Log.d(SynchronizeService.class.getName(), ".  deletedLocalPropertyList: " + deletedLocalPropertyList);
//
//            for(Notice notice : createdLocalNoticeList){
//                noticeLocalDao.delete(notice);
//            }
//            for (Property property : createdLocalPropertyList) {
//                propertyLocalDao.delete(property);
//            }
//            for (Property property : deletedLocalPropertyList) {
//                propertyCloudDao.create(property);
//            }
//
//            Log.d(SynchronizeService.class.getName(), "##################################################");
//            throw e;
//        }
//    }
//
//    private static <Model extends BaseModel> Map<String, Model> getMapOfObjectIdsAndObject(List<Model> models){
//        Map<String, Model> map = new HashMap<String, Model>();
//        for(Model model : models){
//            map.put(model.getObjectId(), model);
//        }
//        return map;
//    }

    public static void updateAccountLocalByCloud(Account acc) {


//        Account cloud = Application.getAccountCloudDao().readWithRelations(acc.getObjectId());
//        Application.getAccountLocalDao();


    }
}
