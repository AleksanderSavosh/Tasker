package com.aleksander.savosh.tasker;

import android.content.Context;
import com.aleksander.savosh.tasker.dao.relational.CloudDao;
import com.aleksander.savosh.tasker.dao.relational.LocalDao;
import com.aleksander.savosh.tasker.dao.relational.SynchronizedDao;
import com.aleksander.savosh.tasker.dao.relational.parse.ParseCloudDaoImpl;
import com.aleksander.savosh.tasker.dao.relational.parse.ParseLocalDaoImpl;
import com.aleksander.savosh.tasker.dao.relational.parse.ParseSynchronizedDaoImpl;
import com.aleksander.savosh.tasker.model.relational.*;
import com.parse.Parse;

public class Application extends android.app.Application {

    private static Context context;
    private static CloudDao<Account> accountCloudDao;
    private static CloudDao<Phone> phoneCloudDao;
    private static LocalDao<LogInData> logInDataLocalDao;
    private static LocalDao<Notice> noticeLocalDao;
    private static CloudDao<Notice> noticeCloudDao;
    private static CloudDao<Property> propertyCloudDao;
    private static LocalDao<Property> propertyLocalDao;

    private static SynchronizedDao<Notice> noticeSynchronizedDao;
    private static SynchronizedDao<Property> propertySynchronizedDao;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        // Enable Local Datastore.
//        Parse.enableLocalDatastore(this);
//        ParseObject.registerSubclass(AccountExx.class);
//        ParseObject.registerSubclass(PhoneExx.class);
        Parse.initialize(this, "cv5X8Il8up7Y4YvrBz6nM6icaf7lBYXfPlwQSmAR", "6fDQLSh7mmIqoZEU5V0BNOrFxHavGEFkVnNDZlrZ");

        accountCloudDao = new ParseCloudDaoImpl<Account>(Account.class);
        phoneCloudDao = new ParseCloudDaoImpl<Phone>(Phone.class);
        logInDataLocalDao = new ParseLocalDaoImpl<LogInData>(LogInData.class);
        noticeLocalDao = new ParseLocalDaoImpl<Notice>(Notice.class);
        noticeCloudDao = new ParseCloudDaoImpl<Notice>(Notice.class);
        propertyCloudDao = new ParseCloudDaoImpl<Property>(Property.class);
        propertyLocalDao = new ParseLocalDaoImpl<Property>(Property.class);

        noticeSynchronizedDao = new ParseSynchronizedDaoImpl<Notice>(noticeCloudDao, noticeLocalDao);
        propertySynchronizedDao = new ParseSynchronizedDaoImpl<Property>(propertyCloudDao, propertyLocalDao);
    }

    public static CloudDao<Account> getAccountCloudDao() {
        return accountCloudDao;
    }

    public static CloudDao<Phone> getPhoneCloudDao() {
        return phoneCloudDao;
    }

    public static LocalDao<LogInData> getLogInDataLocalDao() {
        return logInDataLocalDao;
    }

    public static LocalDao<Notice> getNoticeLocalDao() {
        return noticeLocalDao;
    }

    public static CloudDao<Notice> getNoticeCloudDao() {
        return noticeCloudDao;
    }

    public static CloudDao<Property> getPropertyCloudDao() {
        return propertyCloudDao;
    }

    public static LocalDao<Property> getPropertyLocalDao() {
        return propertyLocalDao;
    }

    public static SynchronizedDao<Notice> getNoticeSynchronizedDao() {
        return noticeSynchronizedDao;
    }

    public static SynchronizedDao<Property> getPropertySynchronizedDao() {
        return propertySynchronizedDao;
    }
}
