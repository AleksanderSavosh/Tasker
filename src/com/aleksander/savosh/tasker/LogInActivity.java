package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.aleksander.savosh.tasker.model.*;
import com.aleksander.savosh.tasker.service.DataNotFoundException;

public class LogInActivity extends Activity {

    private static AutoLogInTask autoLogInTask;
    private class AutoLogInTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                LogInData logInData = Application.getLogInDataLocalDao()
                        .readFirstThrowExceptions(LogInData.builder().build());
                Phone phone = Application.getPhoneCloudDao()
                        .readFirstThrowExceptions(Phone.builder().addNumber(logInData.getPhoneNumber()).build());
                Account account = Application.getAccountCloudDao()
                        .readFirstThrowExceptions(Account.builder().addObjectId(phone.getAccountId()).build());
                if (logInData.getPassword().equals(account.getPassword())) {
                    Application.getLogInDataLocalDao().delete(logInData);
                    Application.getLogInDataLocalDao().createThrowExceptions(LogInData.builder()
                            .addAccountId(account.getObjectId())
                            .addPhoneNumber(phone.getNumber())
                            .addPassword(account.getPassword())
                            .build());
                    return true;
                }
            } catch (Exception e){
                Log.e(getClass().getName(), e.getMessage() == null ? e.toString() : e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean) {
                Intent intent = new Intent(Application.getContext(), MainActivity.class);
                LogInActivity.this.startActivity(intent);
                LogInActivity.this.finish();
                autoLogInTask = null;
            }
        }
    }

    private class LogInResult {
        public Boolean isLogIn = false;
        public String message = "";
    }

    private static LogInTask logInTask;
    private class LogInTask extends AsyncTask<LogInData, Void, LogInResult> {
        @Override
        protected LogInResult doInBackground(LogInData... params) {
            LogInData logInData = params[0];
            LogInResult logInResult = new LogInResult();
            try {
                Phone phone = Application.getPhoneCloudDao()
                        .readFirstThrowExceptions(Phone.builder().addNumber(logInData.getPhoneNumber()).build());
                Account account = Application.getAccountCloudDao()
                        .readFirstThrowExceptions(Account.builder().addObjectId(phone.getAccountId()).build());
                logInResult.isLogIn = logInData.getPassword().equals(account.getPassword());

                if (logInResult.isLogIn) {
                    Application.getLogInDataLocalDao().delete(logInData);
                    Application.getLogInDataLocalDao().createThrowExceptions(LogInData.builder()
                            .addAccountId(account.getObjectId())
                            .addPhoneNumber(phone.getNumber())
                            .addPassword(account.getPassword())
                            .build());
                }
            } catch (DataNotFoundException e) {
                Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
                logInResult.message = LogInActivity.this.getResources().getString(R.string.log_in_invalid_number_or_password_message);
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
                logInResult.message = LogInActivity.this.getResources().getString(R.string.some_error_message);
            }
            return logInResult;
        }

        @Override
        protected void onPostExecute(LogInResult logInResult) {
            if(logInResult.isLogIn){
                Intent intent = new Intent(Application.getContext(), MainActivity.class);
                LogInActivity.this.startActivity(intent);
                LogInActivity.this.finish();
            } else {
                TextView textView = (TextView) LogInActivity.this.findViewById(R.id.login_activity_message);
                textView.setVisibility(View.VISIBLE);
                textView.setText(logInResult.message);
            }
            logInTask = null;
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.login_activity_log_in) {

                if(logInTask == null){
                    TextView message = (TextView) findViewById(R.id.login_activity_message);
                    message.setVisibility(View.VISIBLE);
                    message.setText(LogInActivity.this.getResources().getText(R.string.wait));

                    LogInData logInData = LogInData.builder()
                            .addPhoneNumber(((EditText) findViewById(R.id.login_activity_phone_number)).getText().toString())
                            .addPassword(((EditText) findViewById(R.id.login_activity_password)).getText().toString())
                            .build();

                    logInTask = new LogInTask();
                    logInTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, logInData);
                }

            } else if (v.getId() == R.id.login_activity_sign_up) {
                //sign up
                Intent intent = new Intent(Application.getContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);

        findViewById(R.id.login_activity_log_in).setOnClickListener(clickListener);
        findViewById(R.id.login_activity_sign_up).setOnClickListener(clickListener);

        if(autoLogInTask == null) {
            autoLogInTask = new AutoLogInTask();
            autoLogInTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}