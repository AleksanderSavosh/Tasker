package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.aleksander.savosh.tasker.service.SingUpLogInLogOutService;

import static com.aleksander.savosh.tasker.service.SingUpLogInLogOutService.LogInResult;
import static com.aleksander.savosh.tasker.service.SingUpLogInLogOutService.LogInData;

public class LogInActivity extends Activity {

    private static LogInTask logInTask;
    private class LogInTask extends AsyncTask<LogInData, Void, LogInResult> {
        @Override
        protected LogInResult doInBackground(LogInData... params) {
            return SingUpLogInLogOutService.logIn(params[0]);
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