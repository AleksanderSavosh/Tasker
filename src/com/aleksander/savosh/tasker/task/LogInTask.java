package com.aleksander.savosh.tasker.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.data.LogInData;
import com.aleksander.savosh.tasker.data.LogInResult;
import com.aleksander.savosh.tasker.service.SingUpLogInLogOutService;
import com.aleksander.savosh.tasker.service.SynchronizeService;


public class LogInTask extends AsyncTask<LogInData, Void, LogInResult> {

    private static LogInTask currentTask;
    public static void initTask(LogInData logInData, Activity activity, TextView messageTextView, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new LogInTask();
                currentTask.activity = activity;
                currentTask.messageTextView = messageTextView;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, logInData);
            }
        } else {
            currentTask.activity = activity;
            currentTask.messageTextView = messageTextView;
        }
    }

    private LogInTask(){}
    private Activity activity;
    private TextView messageTextView;

    @Override
    protected LogInResult doInBackground(LogInData... params) {
        return SingUpLogInLogOutService.logIn(params[0]);
    }

    @Override
    protected void onPostExecute(LogInResult logInResult) {
        if (logInResult.isLogIn) {
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(logInResult.message);
        }
        activity = null;
        messageTextView = null;
        currentTask = null;
    }
}