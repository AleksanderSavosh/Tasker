package com.aleksander.savosh.tasker;

import android.content.Context;
import com.aleksander.savosh.tasker.model.Account;
import com.aleksander.savosh.tasker.model.LogInData;
import com.aleksander.savosh.tasker.model.Phone;
import com.aleksander.savosh.tasker.service.CloudDao;
import com.aleksander.savosh.tasker.service.LocalDao;
import com.aleksander.savosh.tasker.service.ParseCloudServiceImpl;
import com.aleksander.savosh.tasker.service.ParseLocalDaoImpl;
import com.parse.Parse;

public class Application extends android.app.Application {

    private static Context context;
    private static CloudDao<Account> accountCloudService;
    private static CloudDao<Phone> phoneCloudService;
    private static LocalDao<LogInData> logInDataLocalDao;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "cv5X8Il8up7Y4YvrBz6nM6icaf7lBYXfPlwQSmAR", "6fDQLSh7mmIqoZEU5V0BNOrFxHavGEFkVnNDZlrZ");

        accountCloudService = new ParseCloudServiceImpl<Account>(Account.class);
        phoneCloudService = new ParseCloudServiceImpl<Phone>(Phone.class);
        logInDataLocalDao = new ParseLocalDaoImpl<LogInData>(LogInData.class);
    }

    public static CloudDao<Account> getAccountCloudService() {
        return accountCloudService;
    }

    public static CloudDao<Phone> getPhoneCloudService() {
        return phoneCloudService;
    }

    public static LocalDao<LogInData> getLogInDataLocalDao() {
        return logInDataLocalDao;
    }
}
