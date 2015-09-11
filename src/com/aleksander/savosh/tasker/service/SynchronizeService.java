package com.aleksander.savosh.tasker.service;


import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Config;

public class SynchronizeService {


    /**
     * синхронизация данных устройства с данными в облаке
     * синхронизация по памяти и в базе данных
     * @param accountCloudId
     */
    public static void synchronizeLocalWithCloud(String accountCloudId){
        if(accountCloudId.equals(Config.ACC_ZERO)){
            return;
        }

        try {
            //get from cloud by id
            Account cloudAccount = Application.getAccountCloudDao().readWithRelationsThrowException(accountCloudId);
            Log.d(SynchronizeService.class.getName(), "cloudAccount: " + cloudAccount);

            //update in local data store
            Account localAccount = Application.getAccountLocalDao().read(accountCloudId);
            if(localAccount == null){
                localAccount = Application.getAccountLocalDao().createWithRelationsThrowException(cloudAccount);
            } else {
                localAccount = Application.getAccountLocalDao().updateWithRelationsThrowException(cloudAccount);
            }
            Log.d(SynchronizeService.class.getName(), "localAccount: " + localAccount);

            //update in memory
            if(Application.instance().getAccounts().containsKey(accountCloudId)) {
                Application.instance().getAccounts().remove(accountCloudId);
            }
            Application.instance().getAccounts().put(accountCloudId, localAccount);

        } catch (DataNotFoundException e) {
            e.printStackTrace();
        } catch (OtherException e) {
            e.printStackTrace();
        } catch (CannotCreateException e) {
            e.printStackTrace();
        }

        Log.d(SynchronizeService.class.getName(), "synchronize local acc with cloud, accId: " + accountCloudId);
    }

    public static void synchronizeCloudWithLocal(Account acc){
        Log.d(SynchronizeService.class.getName(), "synchronize cloud acc with local, accId: " + acc.getObjectId());
    }


}
