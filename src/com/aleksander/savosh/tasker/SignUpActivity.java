package com.aleksander.savosh.tasker;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.aleksander.savosh.tasker.data.SignUpData;
import com.aleksander.savosh.tasker.task.SignUpTask;
import com.aleksander.savosh.tasker.util.ActivityUtil;


public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "--- === ON CREATE SIGN UP ACTIVITY === ---");
        ActivityUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        //TODO тест на интернет конекшн

        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); //TODO сделать предложение ввести это номер
        Log.i(getClass().getName(), "Number 1: " + tMgr.getLine1Number());
        Log.i(getClass().getName(), "Device id: " + tMgr.getDeviceId());

        final EditText phoneEditText = (EditText) findViewById(R.id.sign_up_activity_phone);
        final EditText passwordEditText = (EditText) findViewById(R.id.sign_up_activity_password);
        final EditText repeatPasswordEditText = (EditText) findViewById(R.id.sign_up_activity_repeat_password);
        final CheckBox transferNotesCheckBox = (CheckBox) findViewById(R.id.sign_up_activity_transfer_notes);
        final TextView messageTextView = (TextView) findViewById(R.id.sign_up_activity_message);

        SignUpTask.initTask(null, this, messageTextView, false);

        findViewById(R.id.sign_up_activity_sign_up).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SignUpTask.initTask(new SignUpData() {{
                    number = phoneEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    password2 = repeatPasswordEditText.getText().toString();
                    transferNotes = transferNotesCheckBox.isChecked();
                }}, SignUpActivity.this, messageTextView, true);

            }
        });

        getSupportActionBar().setTitle(R.string.sign_up_activity_title);
    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getName(), "--- === ON DESTROY SIGN UP ACTIVITY === ---");
        super.onDestroy();
    }
}
