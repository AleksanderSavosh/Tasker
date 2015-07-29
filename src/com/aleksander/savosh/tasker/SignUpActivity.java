package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.aleksander.savosh.tasker.dao.CloudDao;
import com.aleksander.savosh.tasker.dao.LocalDao;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.model.*;
import com.aleksander.savosh.tasker.service.NoticeService;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends Activity {

    private class SignUpData {
        public String number;
        public String password;
        public String password2;
        public Boolean transferNotes;
    }

    private class SignUpResult {
        public Boolean isSignUp;
        public String message;
    }

    private static SignUpTask signUpTask;

    private class SignUpTask extends AsyncTask<SignUpData, Void, SignUpResult> {

        @Override
        protected SignUpResult doInBackground(SignUpData... params) {
            CloudDao<Phone> phoneCloudDao = Application.getPhoneCloudDao();
            CloudDao<Account> accountCloudDao = Application.getAccountCloudDao();
            LocalDao<LogInData> logInDataLocalDao = Application.getLogInDataLocalDao();
            LocalDao<Notice> noticeLocalDao = Application.getNoticeLocalDao();
            CloudDao<Notice> noticeCloudDao = Application.getNoticeCloudDao();
            LocalDao<Property> propertyLocalDao = Application.getPropertyLocalDao();
            CloudDao<Property> propertyCloudDao = Application.getPropertyCloudDao();

            SignUpData data = params[0];
            SignUpResult result = new SignUpResult();
            result.isSignUp = false;

            //проверка на правильный формат телефона


            //проверка на совпадение паролей
            if (!data.password.equals(data.password2)) {
                result.message = getResources().getString(R.string.passwords_doesnt_equal);
                return result;
            }
            data.password = StringUtil.encodePassword(data.password);


            //проверка что такого телефона еще нет
            try {
                phoneCloudDao.readFirstThrowExceptions(Phone.builder().setNumber(data.number).build());
                result.message = getResources().getString(R.string.this_phone_already_exist);
                return result;
            } catch (DataNotFoundException e) {

            } catch (Exception e) {
                result.message = getResources().getString(R.string.some_error_message);
                return result;
            }

            Account account = null;
            Phone phone = null;
            LogInData logInData = null;
            List<Notice> updatedLocalNoticeList = new ArrayList<Notice>();
            List<Notice> createdCloudNoticeList = new ArrayList<Notice>();
            List<Property> updatedLocalPropertyList = new ArrayList<Property>();
            List<Property> createdCloudPropertyList = new ArrayList<Property>();
            try {
                account = accountCloudDao.createThrowExceptions(Account.builder()
                        .setPassword(data.password)
                        .build());

                phone = phoneCloudDao.createThrowExceptions(Phone.builder()
                        .setAccountId(account.getObjectId())
                        .setNumber(data.number)
                        .build());

                logInData = logInDataLocalDao.createThrowExceptions(LogInData.builder()
                        .setAccountId(account.getObjectId())
                        .setPhoneNumber(phone.getNumber())
                        .setPassword(data.password)
                        .build());

                if(data.transferNotes) {
                    List<Notice> localNoticeList = noticeLocalDao.read(Notice.builder()
                            .setAccountId(NoticeService.DEFAULT_ACCOUNT_ID)
                            .build());

                    for (Notice localNotice : localNoticeList) {
                        Notice cloudNotice = noticeCloudDao.createThrowExceptions(Notice.builder(localNotice)
                                .setAccountId(account.getObjectId())
                                .build());
                        createdCloudNoticeList.add(cloudNotice);
                        noticeLocalDao.deleteThrowExceptions(localNotice);
                        noticeLocalDao.createThrowExceptions(cloudNotice);
                        updatedLocalNoticeList.add(localNotice);

                        List<Property> localPropertyList = propertyLocalDao.readThrowExceptions(Property.builder()
                                .setNoticeId(localNotice.getObjectId())
                                .build());

                        for (Property localProperty : localPropertyList) {
                            Property cloudProperty = propertyCloudDao.createThrowExceptions(Property
                                    .builder(localProperty).setNoticeId(cloudNotice.getObjectId()).build());
                            createdCloudPropertyList.add(cloudProperty);
                            propertyLocalDao.deleteThrowExceptions(localProperty);
                            propertyLocalDao.createThrowExceptions(cloudProperty);
                            updatedLocalPropertyList.add(localProperty);
                        }
                    }
                }

                result.isSignUp = true;
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage());
                Log.d(getClass().getName(), e.getMessage(), e);

                Log.d(getClass().getName(), "#################### ROLLBACK ####################");
                Log.d(getClass().getName(), ".    delete cloud account: " + account);
                Log.d(getClass().getName(), ".      delete cloud phone: " + phone);
                Log.d(getClass().getName(), ".               logInData: " + logInData);
                Log.d(getClass().getName(), ".  updatedLocalNoticeList: " + updatedLocalNoticeList);
                Log.d(getClass().getName(), ".  createdCloudNoticeList: " + createdCloudNoticeList);
                Log.d(getClass().getName(), ".updatedLocalPropertyList: " + updatedLocalPropertyList);
                Log.d(getClass().getName(), ".createdCloudPropertyList: " + logInData);

                if (account != null) {
                    accountCloudDao.delete(Account.builder().setObjectId(account.getObjectId()).build());
                }
                if (phone != null) {
                    phoneCloudDao.delete(Phone.builder().setObjectId(phone.getObjectId()).build());
                }
                if (logInData != null) {
                    logInDataLocalDao.delete(LogInData.builder()
                            .setObjectId(logInData.getObjectId())
                            .build());
                }

                if (!updatedLocalNoticeList.isEmpty()) {
                    for (Notice notice : updatedLocalNoticeList) {
                        noticeLocalDao.create(notice);
                    }
                }

                if (!createdCloudNoticeList.isEmpty()) {
                    for (Notice notice : createdCloudNoticeList) {
                        noticeCloudDao.delete(notice);
                    }
                }

                if (!updatedLocalPropertyList.isEmpty()) {
                    for (Property property : updatedLocalPropertyList) {
                        propertyLocalDao.create(property);
                    }
                }

                if (!createdCloudPropertyList.isEmpty()) {
                    for (Property property : createdCloudPropertyList) {
                        propertyCloudDao.delete(property);
                    }
                }
                Log.d(getClass().getName(), "##################################################");
                result.message = getResources().getString(R.string.some_error_message);
                return result;
            }

            return result;
        }

        @Override
        protected void onPostExecute(SignUpResult result) {
            if (result.isSignUp) {
                //переход на след активити
                Intent intent = new Intent(Application.getContext(), MainActivity.class);
                SignUpActivity.this.startActivity(intent);
                SignUpActivity.this.finish();
            } else {
                ((TextView) findViewById(R.id.sign_up_activity_message)).setText(result.message);
            }
            signUpTask = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        Log.i(getClass().getName(), "Number 1: " + tMgr.getLine1Number());
        Log.i(getClass().getName(), "Device id: " + tMgr.getDeviceId());

        final EditText phoneEditText = (EditText) findViewById(R.id.sign_up_activity_phone);
        final EditText passwordEditText = (EditText) findViewById(R.id.sign_up_activity_password);
        final EditText repeatPasswordEditText = (EditText) findViewById(R.id.sign_up_activity_repeat_password);
        final TextView message = (TextView) findViewById(R.id.sign_up_activity_message);
        final CheckBox transferNotes = (CheckBox) findViewById(R.id.sign_up_activity_transfer_notes);

        findViewById(R.id.sign_up_activity_sign_up).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (signUpTask != null) {
                    return;
                }

                SignUpData signUpData = new SignUpData();
                signUpData.number = phoneEditText.getText().toString();
                signUpData.password = passwordEditText.getText().toString();
                signUpData.password2 = repeatPasswordEditText.getText().toString();
                signUpData.transferNotes = transferNotes.isChecked();
                signUpTask = new SignUpTask();
                signUpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, signUpData);
            }
        });
    }
}
