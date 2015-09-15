package com.aleksander.savosh.tasker.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.method.KeyListener;
import android.view.View;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.data.LogInData;
import com.aleksander.savosh.tasker.data.LogInResult;
import com.aleksander.savosh.tasker.service.SingUpLogInLogOutService;
import com.aleksander.savosh.tasker.task.holder.LogInTaskHolder;


public class LogInTask extends AsyncTask<LogInData, Void, LogInResult> {

    private static LogInTask currentTask;
    public static void initTask(LogInData logInData, LogInTaskHolder holder, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new LogInTask();
                currentTask.holder = holder;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, logInData);
            }
        } else {
            currentTask.holder = holder;
            currentTask.lock();
        }
    }

    private LogInTask(){}
    private LogInTaskHolder holder;

    @Override
    protected void onPreExecute() {
        lock();
    }

    private void lock(){
        holder.numberEditText.setTag(holder.numberEditText.getKeyListener());
        holder.numberEditText.setKeyListener(null);
        holder.numberEditText.setClickable(false);

        holder.passwordEditText.setTag(holder.passwordEditText.getKeyListener());
        holder.passwordEditText.setKeyListener(null);
        holder.passwordEditText.setClickable(false);

        holder.rememberMeCheckBox.setClickable(false);
        holder.logInButton.setClickable(false);
        holder.signUpButton.setClickable(false);
        holder.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected LogInResult doInBackground(LogInData... params) {
        return SingUpLogInLogOutService.logIn(params[0]);
    }

    @Override
    protected void onPostExecute(LogInResult logInResult) {
        if (logInResult.isLogIn) {
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            holder.activity.startActivity(intent);
            holder.activity.finish();
        }

        holder.progressBar.setVisibility(View.GONE);
        holder.numberEditText.setKeyListener((KeyListener) holder.numberEditText.getTag());
        holder.numberEditText.setClickable(true);
        holder.passwordEditText.setKeyListener((KeyListener) holder.passwordEditText.getTag());
        holder.passwordEditText.setClickable(true);
        holder.rememberMeCheckBox.setClickable(true);
        holder.logInButton.setClickable(true);
        holder.signUpButton.setClickable(true);

        holder = null;
        currentTask = null;
    }
}