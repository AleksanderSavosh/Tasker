package com.aleksander.savosh.tasker.service;


import android.util.Log;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Config;

public class SynchronizeService {


    /**
     * синхронизация данных устройства с данными в облаке
     * синхронизация по памяти и в базе данных
     * @param acc
     */
    public static void synchronizeLocalWithCloud(Account acc){
        if(acc.getObjectId().equals(Config.ACC_ZERO)){
            return;
        }

        Log.d(SynchronizeService.class.getName(), "synchronize local acc with cloud, accId: " + acc.getObjectId());
    }

    public static void synchronizeCloudWithLocal(Account acc){
        Log.d(SynchronizeService.class.getName(), "synchronize cloud acc with local, accId: " + acc.getObjectId());
    }


}
