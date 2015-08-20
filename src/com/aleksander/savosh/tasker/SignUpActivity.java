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
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Phone;
import com.aleksander.savosh.tasker.service.SynchronizeService;

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
//            CloudDao<Phone> phoneCloudDao = Application.getPhoneCloudDao();
//            CloudDao<Account> accountCloudDao = Application.getAccountCloudDao();
//            LocalDao<LogInData> logInDataLocalDao = Application.getLogInDataLocalDao();
//
//
//            SignUpData data = params[0];
            SignUpResult result = new SignUpResult();
            result.isSignUp = false;
//
//            //проверка на правильный формат телефона
//
//
//            //проверка на совпадение паролей
//            if (!data.password.equals(data.password2)) {
//                result.message = getResources().getString(R.string.passwords_doesnt_equal);
//                return result;
//            }
//            data.password = StringUtil.encodePassword(data.password);
//
//
//            //проверка что такого телефона еще нет
//            try {
//                phoneCloudDao.readFirstThrowExceptions(Phone.builder().setNumber(data.number).build());
//                result.message = getResources().getString(R.string.this_phone_already_exist);
//                return result;
//            } catch (DataNotFoundException normalSituationException) {
//
//            } catch (Exception e) {
//                result.message = getResources().getString(R.string.some_error_message);
//                return result;
//            }
//
//            Account account = null;
//            Phone phone = null;
//            LogInData logInData = null;
//            try {
//                account = accountCloudDao.createThrowExceptions(Account.builder()
//                        .setPassword(data.password)
//                        .build());
//
//                phone = phoneCloudDao.createThrowExceptions(Phone.builder()
//                        .setAccountId(account.getObjectId())
//                        .setNumber(data.number)
//                        .build());
//
//                logInData = logInDataLocalDao.createThrowExceptions(LogInData.builder()
//                        .setAccountId(account.getObjectId())
//                        .setPhoneNumber(phone.getNumber())
//                        .setPassword(data.password)
//                        .build());
//
//                if(data.transferNotes) {
//                    SynchronizeService.transferLocalNoticesToCloud(account.getObjectId());
//                }
//                result.isSignUp = true;
//            } catch (Exception e) {
//                Log.e(getClass().getName(), e.getMessage());
//                Log.d(getClass().getName(), e.getMessage(), e);
//
//                Log.d(getClass().getName(), "#################### ROLLBACK ####################");
//                Log.d(getClass().getName(), ".    delete cloud account: " + account);
//                Log.d(getClass().getName(), ".      delete cloud phone: " + phone);
//                Log.d(getClass().getName(), ".               logInData: " + logInData);
//
//
//                if (account != null) {
//                    accountCloudDao.delete(Account.builder().setObjectId(account.getObjectId()).build());
//                }
//                if (phone != null) {
//                    phoneCloudDao.delete(Phone.builder().setObjectId(phone.getObjectId()).build());
//                }
//                if (logInData != null) {
//                    logInDataLocalDao.delete(LogInData.builder()
//                            .setObjectId(logInData.getObjectId())
//                            .build());
//                }
//
//                Log.d(getClass().getName(), "##################################################");
//                result.message = getResources().getString(R.string.some_error_message);
//                return result;
//            }

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
                ((TextView) findViewById(R.id.sign_up_activity_message)).setVisibility(View.VISIBLE);
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
