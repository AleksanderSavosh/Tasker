package com.aleksander.savosh.tasker.service;


import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.R;
import com.aleksander.savosh.tasker.data.LogInData;
import com.aleksander.savosh.tasker.data.LogInResult;
import com.aleksander.savosh.tasker.data.SignUpData;
import com.aleksander.savosh.tasker.data.SignUpResult;
import com.aleksander.savosh.tasker.util.StringUtil;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.dao.object.KeyValue;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Phone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingUpLogInLogOutService {


    public static SignUpResult singUp(SignUpData signUpData){
        SignUpResult result = new SignUpResult();
        result.isSignUp = false;

        //проверка на правильный формат телефона


        //проверка на совпадение паролей
        if (!signUpData.password.equals(signUpData.password2)) {
            result.message = Application.getContext().getResources().getString(R.string.passwords_doesnt_equal);
            return result;
        }
        signUpData.password = StringUtil.encodePassword(signUpData.password);


        //проверка что такого телефона еще нет
        try {
            List<Phone> phones = Application.getPhoneCloudDao().readBy(new KeyValue("number", signUpData.number));
            if(!phones.isEmpty()){
                result.message = Application.getContext().getResources().getString(R.string.this_phone_already_exist);
                return result;
            }

            Account account = new Account(signUpData.password, Arrays.asList(new Phone(signUpData.number)), null);

            if(signUpData.transferNotes) {
                account.setNotices(new ArrayList<Notice>(
                        Application
                        .instance()
                        .getAccounts()
                        .get(Config.ACC_ZERO)
                        .getNotices()));

                Application.instance().getAccounts().remove(Config.ACC_ZERO);
                Application.getAccountLocalDao().deleteWithRelationsThrowException(Config.ACC_ZERO);
            }

            account = Application.getAccountCloudDao().createWithRelationsThrowException(account);
            Application.instance().getAccounts().put(account.getObjectId(), account);

//            if(!account.getObjectId().equals(Config.ACC_ZERO)) {
//                Application.instance().getConfig().accountId = account.getObjectId();
//            }

            Config config = Application.getConfigLocalCrudDao().read(Config.ID);
            if(config == null){
                config = new Config();
                config.accountId = account.getObjectId();
                Application.getConfigLocalCrudDao().createThrowException(config);
            } else {
                config.accountId = account.getObjectId();
                Application.getConfigLocalCrudDao().updateThrowException(config);
            }
            Application.instance().setConfig(config);


            result.isSignUp = true;
        } catch (Exception e) {
            Log.e(SingUpLogInLogOutService.class.getName(), e.getMessage() != null ? e.getMessage() : e.toString());
            result.message = Application.getContext().getResources().getString(R.string.some_error_message);
        }
        return result;
    }

    public static LogInResult logIn(LogInData logInData){
        Log.d(SingUpLogInLogOutService.class.getName(), "--- === LOG IN === ---");
        LogInResult logInResult = new LogInResult();
        try {

            List<Phone> phones = Application.getPhoneCloudDao()
                    .readByThrowException(new KeyValue("number", logInData.number));

            if(phones.size() != 1){
                throw new DataNotFoundException("Phone list size is not equal 1");
            }

            Phone phone = phones.get(0);
            Account account = (Account) Application.getPhoneCloudDao()
                    .getParentWithRelationsThrowException(Account.class, phone);

            logInResult.isLogIn = StringUtil.encodePassword(logInData.password).equals(account.getPassword());
            if(logInResult.isLogIn){

                Config config = Application.getConfigLocalCrudDao().read(Config.ID);
                if(config == null){
                    config = new Config();
                    config.accountId = account.getObjectId();
                    config.rememberMe = logInData.rememberMe;
                    Application.getConfigLocalCrudDao().createThrowException(config);
                } else {
                    config.accountId = account.getObjectId();
                    config.rememberMe = logInData.rememberMe;
                    Application.getConfigLocalCrudDao().updateThrowException(config);
                }
                Application.instance().setConfig(config);

                //синхронизация локал с клауд
                SynchronizeService.synchronizeLocalWithCloud(account.getObjectId());

            } else {
                logInResult.message = Application.getContext().getResources().getString(R.string.log_in_invalid_number_or_password_message);
            }

        } catch (DataNotFoundException e) {
            Log.e(SingUpLogInLogOutService.class.getName(), e.getMessage() != null ? e.getMessage() : e.toString());
            logInResult.message = Application.getContext().getResources().getString(R.string.log_in_invalid_number_or_password_message);
        } catch (Exception e) {
            Log.e(SingUpLogInLogOutService.class.getName(), e.getMessage() != null ? e.getMessage() : e.toString());
            logInResult.message = Application.getContext().getResources().getString(R.string.some_error_message);
        } finally {
            Log.d(SingUpLogInLogOutService.class.getName(), "RESULT: " + logInResult);
            Log.d(SingUpLogInLogOutService.class.getName(), "--- === LOG IN END === ---");
        }

        return logInResult;

    }

    public static class LogOutResult {
        public Boolean isLogOut = false;
        public String message = "";
    }

    public static LogOutResult logOut(){
        Log.d(SingUpLogInLogOutService.class.getName(), "Log out operation");
        LogOutResult logOutResult = new LogOutResult();
        if(!StringUtil.isEmpty(Application.instance().getConfig().accountId)) {
            try {
                Application.getConfigLocalCrudDao().deleteThrowException(Config.ID);
                Application.instance().setConfig(new Config());
                logOutResult.isLogOut = true;
            } catch (DataNotFoundException e) {
                Log.e(SingUpLogInLogOutService.class.getName(), e.getMessage() != null ? e.getMessage() : e.toString());
                Log.d(SingUpLogInLogOutService.class.getName(), e.getMessage() != null ? e.getMessage() : e.toString(), e);
                logOutResult.message = "Log out operation not successful";
            } catch (OtherException e) {
                Log.e(SingUpLogInLogOutService.class.getName(), e.getMessage() != null ? e.getMessage() : e.toString());
                Log.d(SingUpLogInLogOutService.class.getName(), e.getMessage() != null ? e.getMessage() : e.toString(), e);
                logOutResult.message = "Log out operation not successful";
            }
        }
        return logOutResult;
    }

}
