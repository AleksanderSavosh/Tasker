package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.aleksander.savosh.tasker.data.LogInData;
import com.aleksander.savosh.tasker.task.LogInTask;
import com.aleksander.savosh.tasker.task.holder.LogInTaskHolder;

public class LogInActivity extends Activity {

    private EditText numberEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button logInButton;
    private Button signUpButton;
    private View progressBar;
    private LogInTaskHolder holder;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.login_activity_log_in) {

                Application.getAsyncTaskManager().startTask(LogInTask.class, holder, new LogInData() {{
                    number = numberEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    rememberMe = rememberMeCheckBox.isChecked();
                }});

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

        numberEditText = ((EditText) findViewById(R.id.login_activity_phone_number));
        passwordEditText = ((EditText) findViewById(R.id.login_activity_password));
        rememberMeCheckBox = ((CheckBox) findViewById(R.id.login_activity_remember_me));
        logInButton = (Button) findViewById(R.id.login_activity_log_in);
        signUpButton = (Button) findViewById(R.id.login_activity_sign_up);
        progressBar = findViewById(R.id.login_activity_progress_bar);
        holder = new LogInTaskHolder(){{
            this.activity = LogInActivity.this;
            this.numberEditText = LogInActivity.this.numberEditText;
            this.passwordEditText = LogInActivity.this.passwordEditText;
            this.rememberMeCheckBox = LogInActivity.this.rememberMeCheckBox;
            this.logInButton = LogInActivity.this.logInButton;
            this.signUpButton = LogInActivity.this.signUpButton;
            this.progressBar = LogInActivity.this.progressBar;
        }};

        Application.getAsyncTaskManager().updateTask(LogInTask.class, holder);

        findViewById(R.id.login_activity_log_in).setOnClickListener(clickListener);
        findViewById(R.id.login_activity_sign_up).setOnClickListener(clickListener);
    }

}