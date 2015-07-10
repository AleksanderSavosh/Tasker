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
import com.parse.ParseException;

public class LogInActivity extends Activity {

    private static AutoLogInTask autoLogInTask;
    private class AutoLogInTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                LogInData logInData = Application.getLogInDataLocalDao().readFirstThrowExceptions(new LogInData());
                Phone phone = Application.getPhoneCloudService().readFirstThrowExceptions(
                        new PhoneBuilder().addNumber(logInData.getPhoneNumber()));
                Account account = Application.getAccountCloudService().readFirstThrowExceptions(
                        new AccountBuilder().addObjectId(phone.getAccountId()));
                if (logInData.getPassword().equals(account.getPassword())) {
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
        public Boolean isLogIn;
        public String message;
    }

    private static LogInTask logInTask;
    private class LogInTask extends AsyncTask<LogInData, Void, LogInResult> {
        @Override
        protected LogInResult doInBackground(LogInData... params) {
            LogInData logInData = params[0];
            LogInResult logInResult = new LogInResult();
            logInResult.isLogIn = false;
            logInResult.message = LogInActivity.this.getResources()
                    .getString(R.string.log_in_invalid_number_or_password_message);
            try {
                Phone phone = Application.getPhoneCloudService()
                        .readFirstThrowExceptions(new PhoneBuilder().addNumber(logInData.getPhoneNumber()));
                Account account = Application.getAccountCloudService()
                        .readFirstThrowExceptions(new AccountBuilder().addObjectId(phone.getAccountId()));
                logInResult.isLogIn = logInData.getPassword().equals(account.getPassword());

                if(logInResult.isLogIn){
//                    Application.getLogInDataLocalDao().delete(logInData);
                    Application.getLogInDataLocalDao().create(logInData);
                }
            } catch (Exception e) {
                if (e instanceof ParseException && ((ParseException) e).getCode() != 101) {
                    Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
                    logInResult.message = LogInActivity.this.getResources().getString(R.string.log_in_error_message);
                }
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

                    LogInData logInData = new LogInData();
                    logInData.setPhoneNumber(
                            ((EditText) findViewById(R.id.login_activity_phone_number)).getText().toString());
                    logInData.setPassword(
                            ((EditText) findViewById(R.id.login_activity_password)).getText().toString());

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