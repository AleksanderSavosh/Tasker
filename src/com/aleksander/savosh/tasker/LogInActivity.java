package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.aleksander.savosh.tasker.data.LogInData;
import com.aleksander.savosh.tasker.task.LogInTask;

public class LogInActivity extends Activity {

    private EditText numberEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private TextView messageTextView;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.login_activity_log_in) {

                LogInTask.initTask(new LogInData(){{
                    number = numberEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    rememberMe = rememberMeCheckBox.isChecked();
                }}, LogInActivity.this, messageTextView, true);

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

        messageTextView = (TextView) findViewById(R.id.login_activity_message);
        numberEditText = ((EditText) findViewById(R.id.login_activity_phone_number));
        passwordEditText = ((EditText) findViewById(R.id.login_activity_password));
        rememberMeCheckBox = ((CheckBox) findViewById(R.id.login_activity_remember_me));

        LogInTask.initTask(null, LogInActivity.this, messageTextView, false);

        findViewById(R.id.login_activity_log_in).setOnClickListener(clickListener);
        findViewById(R.id.login_activity_sign_up).setOnClickListener(clickListener);
    }

}