package com.aleksander.savosh.tasker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.dao.object.CrudDao;
import com.aleksander.savosh.tasker.dao.object.parse.ParseCloudCrudDaoImpl;
import com.aleksander.savosh.tasker.dao.object.parse.ParseLocalCrudDaoImpl;
import com.aleksander.savosh.tasker.model.object.*;
import com.parse.Parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Application extends android.app.Application {

    private Map<String, Account> accounts = new HashMap<String, Account>();
    private Config config;

    private static Context context;

    private static CrudDao<Config, String> configLocalCrudDao;

    private static CrudDao<Account, String> accountCloudDao;
    private static CrudDao<Account, String> accountLocalDao;

    private static CrudDao<Phone, String> phoneCloudDao;
    private static CrudDao<Phone, String> phoneLocalDao;

    private static CrudDao<Notice, String> noticeCloudDao;
    private static CrudDao<Notice, String> noticeLocalDao;

    private static CrudDao<Property, String> propertyCloudDao;
    private static CrudDao<Property, String> propertyLocalDao;


    public static Context getContext() {
        return context;
    }

    public static Application instance(){
        return (Application) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "cv5X8Il8up7Y4YvrBz6nM6icaf7lBYXfPlwQSmAR", "6fDQLSh7mmIqoZEU5V0BNOrFxHavGEFkVnNDZlrZ");

        configLocalCrudDao = new ParseLocalCrudDaoImpl<Config>(Config.class);

        accountCloudDao = new ParseCloudCrudDaoImpl<Account>(Account.class);
        phoneCloudDao = new ParseCloudCrudDaoImpl<Phone>(Phone.class);
        noticeCloudDao = new ParseCloudCrudDaoImpl<Notice>(Notice.class);
        propertyCloudDao = new ParseCloudCrudDaoImpl<Property>(Property.class);

        accountLocalDao = new ParseLocalCrudDaoImpl<Account>(Account.class);
        phoneLocalDao = new ParseLocalCrudDaoImpl<Phone>(Phone.class);
        noticeLocalDao = new ParseLocalCrudDaoImpl<Notice>(Notice.class);
        propertyLocalDao = new ParseLocalCrudDaoImpl<Property>(Property.class);

        if(true){
            accountLocalDao.deleteWithRelations(Config.ACC_ZERO);
        }

        config = configLocalCrudDao.readWithRelations(Config.ID);
        if(config == null){
            config = new Config();
        }
        if(config.rememberMe == null || !config.rememberMe){
            config.rememberMe = false;
            config.accountId = null;
        }

        for(Account acc : accountLocalDao.readAllWithRelations()){
            accounts.put(acc.getObjectId(), acc);
        }

        if(!accounts.containsKey(Config.ACC_ZERO)){
            accounts.put(Config.ACC_ZERO,
                    new Account(Config.ACC_ZERO, null, null, null, new ArrayList<Phone>(), new ArrayList<Notice>()));
        }
    }

    public static CrudDao<Config, String> getConfigLocalCrudDao() {
        return configLocalCrudDao;
    }

    public static CrudDao<Account, String> getAccountCloudDao() {
        return accountCloudDao;
    }

    public static CrudDao<Account, String> getAccountLocalDao() {
        return accountLocalDao;
    }

    public static CrudDao<Phone, String> getPhoneCloudDao() {
        return phoneCloudDao;
    }

    public static CrudDao<Phone, String> getPhoneLocalDao() {
        return phoneLocalDao;
    }

    public static CrudDao<Notice, String> getNoticeCloudDao() {
        return noticeCloudDao;
    }

    public static CrudDao<Notice, String> getNoticeLocalDao() {
        return noticeLocalDao;
    }

    public static CrudDao<Property, String> getPropertyCloudDao() {
        return propertyCloudDao;
    }

    public static CrudDao<Property, String> getPropertyLocalDao() {
        return propertyLocalDao;
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
