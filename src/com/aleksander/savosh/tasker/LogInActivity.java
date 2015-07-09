package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.aleksander.savosh.tasker.model.Account;
import com.aleksander.savosh.tasker.model.Phone;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class LogInActivity extends Activity {

    private static AutoLogInTask autoLogInTask;
    private class AutoLogInTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
//            try {
//                ParseQuery getLogInInfo = ParseQuery.getQuery("LogInInfo");
//                getLogInInfo.fromLocalDatastore();
//                ParseObject logInInfoObject = getLogInInfo.getFirst();
                String number = "+380639531649"; //logInInfoObject.getString("number");
                String password = "Password";//logInInfoObject.getString("password");

                Phone phone = Application.getPhoneCloudService().read(number);
                Account account = Application.getAccountCloudService().read(phone.getAccountId());

                if(password.equals(account.getPassword())){
                    return true;
                }

//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
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

    private class LogInData {
        public String number;
        public String password;
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
                ParseQuery<ParseObject> getPhone = ParseQuery.getQuery("Phone");
                getPhone.whereEqualTo("number", logInData.number);
                ParseObject phoneObject = getPhone.getFirst();
                String accountId = phoneObject.getString("accountId");

                ParseQuery<ParseObject> getAccount = ParseQuery.getQuery("Account");
                ParseObject accountObject = getAccount.get(accountId);
                String accountPassword = accountObject.getString("password");
                logInResult.isLogIn = logInData.password.equals(accountPassword);

            } catch (ParseException e) {
                if(e.getCode() != 101) {
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
                    logInData.number = ((EditText) findViewById(R.id.login_activity_phone_number)).getText().toString();
                    logInData.password = ((EditText) findViewById(R.id.login_activity_password)).getText().toString();

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