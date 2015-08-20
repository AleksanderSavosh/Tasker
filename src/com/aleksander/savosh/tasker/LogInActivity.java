package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.object.KeyValue;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Phone;

import java.util.List;

public class LogInActivity extends Activity {

    private class LogInData {
        public String number;
        public String password;
        public Boolean rememberMe;

        public LogInData(String number, String password, Boolean rememberMe) {
            this.number = number;
            this.password = password;
            this.rememberMe = rememberMe;
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

                List<Phone> phones = Application.getPhoneCloudDao()
                        .readByThrowException(new KeyValue("number", logInData.number));

                if(phones.size() != 1){
                    throw new DataNotFoundException("Phone list size is not equal 1");
                }

                Phone phone = phones.get(0);
                Account account = (Account) Application.getPhoneCloudDao()
                        .getParentWithRelationsThrowException(Account.class, phone);




//                Phone phone = Application.getPhoneCloudDao()
//                        .readFirstThrowExceptions(Phone.builder().setNumber(logInData.getPhoneNumber()).build());
//                Account account = Application.getAccountCloudDao()
//                        .readFirstThrowExceptions(Account.builder().setObjectId(phone.getAccountId()).build());
//                logInResult.isLogIn = logInData.getPassword().equals(account.getPassword());
//
//                if (logInResult.isLogIn) {
//                    Application.getLogInDataLocalDao().delete(logInData);
//                    Application.getLogInDataLocalDao().createThrowExceptions(LogInData.builder()
//                            .setAccountId(account.getObjectId())
//                            .setPhoneNumber(phone.getNumber())
//                            .setPassword(account.getPassword())
//                            .setRememberMe(logInData.getRememberMe())
//                            .build());
//                }
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
            if (logInResult.isLogIn) {
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

                if (logInTask == null) {
                    TextView message = (TextView) findViewById(R.id.login_activity_message);
                    message.setVisibility(View.VISIBLE);
                    message.setText(LogInActivity.this.getResources().getText(R.string.wait));

                    String number = ((EditText) findViewById(R.id.login_activity_phone_number)).getText().toString();
                    String password = ((EditText) findViewById(R.id.login_activity_password)).getText().toString();
                    Boolean rememberMe = ((CheckBox) findViewById(R.id.login_activity_remember_me)).isChecked();


                    LogInData logInData = new LogInData(number, StringUtil.encodePassword(password), rememberMe);

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

    public static void main(String[] args) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);

        findViewById(R.id.login_activity_log_in).setOnClickListener(clickListener);
        findViewById(R.id.login_activity_sign_up).setOnClickListener(clickListener);
    }

}