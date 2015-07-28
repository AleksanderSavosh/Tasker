package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class SignUpActivity extends Activity {

    private class SignUpData {
        public String number;
        public String password;
    }

    private class SignUpResult {
        public Boolean isSignUp;
        public String message;
    }

    private class SignUpTask extends AsyncTask<SignUpData, Void, SignUpResult> {

        @Override
        protected SignUpResult doInBackground(SignUpData... params) {
            SignUpData data = params[0];

            //проверка что такого телефона еще нет
            //запись данных в облако
            //запись данных для входа в локальное хранилище

            SignUpResult result = new SignUpResult();
            result.isSignUp = false;

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Phone");
            query.whereEqualTo("number", data.number);
            try {
                query.getFirst();
                result.message = Application.getContext().getResources().getString(R.string.this_phone_already_exist);
            } catch (ParseException e) {
                if(e.getCode() == 101) {//"no results found for query"
                    try {
                        ParseObject saveAccount = new ParseObject("Account");
                        saveAccount.put("password", data.password);
                        saveAccount.save();

                        ParseObject savePhone = new ParseObject("Phone");
                        savePhone.put("number", data.number);
                        savePhone.put("accountId", saveAccount.getObjectId());
                        savePhone.save();

                        ParseObject saveLogInInfo = new ParseObject("LogInInfo");
                        saveLogInInfo.put("number", data.number);
                        saveLogInInfo.put("password", data.password);
                        saveLogInInfo.pin();

                        result.isSignUp = true;
                    } catch (ParseException e1) {
                        result.message = e.getMessage();
                    }
                } else {
                    result.message = e.getMessage();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(SignUpResult result) {
            if(result.isSignUp){
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

    private static SignUpTask signUpTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        Log.i(getClass().getName(),"Number 1: " + tMgr.getLine1Number());
        Log.i(getClass().getName(),"Device id: " + tMgr.getDeviceId());

        final EditText phoneEditText = (EditText) findViewById(R.id.sign_up_activity_phone);
        final EditText passwordEditText = (EditText) findViewById(R.id.sign_up_activity_password);
        final EditText repeatPasswordEditText = (EditText) findViewById(R.id.sign_up_activity_repeat_password);
        final TextView message = (TextView) findViewById(R.id.sign_up_activity_message);

        findViewById(R.id.sign_up_activity_sign_up).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(signUpTask != null){
                    return;
                }


                //проверка на правильный формат телефона


                //проверка на совпадение паролей
                if(!passwordEditText.getText().toString().equals(repeatPasswordEditText.getText().toString())){
                    message.setText(R.string.passwords_doesnt_equal);
                    return;
                }

                SignUpData signUpData = new SignUpData();
                signUpData.number = phoneEditText.getText().toString();
                signUpData.password = StringUtil.encodePassword(passwordEditText.getText().toString());
                signUpTask = new SignUpTask();
                signUpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, signUpData);
            }
        });
    }
}
