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
import com.aleksander.savosh.tasker.service.SingUpLogInLogOutService;

import static com.aleksander.savosh.tasker.service.SingUpLogInLogOutService.SignUpData;
import static com.aleksander.savosh.tasker.service.SingUpLogInLogOutService.SignUpResult;

public class SignUpActivity extends Activity {



    private static SignUpTask signUpTask;

    private class SignUpTask extends AsyncTask<SignUpData, Void, SignUpResult> {

        @Override
        protected SignUpResult doInBackground(SignUpData... params) {
            SignUpData data = params[0];
            return SingUpLogInLogOutService.singUp(data);
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
