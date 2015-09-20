package com.aleksander.savosh.tasker.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.method.KeyListener;
import android.view.View;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.R;
import com.aleksander.savosh.tasker.data.LogInData;
import com.aleksander.savosh.tasker.data.LogInResult;
import com.aleksander.savosh.tasker.service.SingUpLogInLogOutService;
import com.aleksander.savosh.tasker.task.holder.LogInTaskHolder;


public class LogInTask extends AbstractAsyncTask<LogInData, Void, LogInResult, LogInTaskHolder> {

    @Override
    protected LogInResult doInBackground(LogInData... params) {
        LogInResult result = SingUpLogInLogOutService.logIn(params[0]);
        waitIfNeed();
        return result;
    }

    @Override
    protected void onPostExecute(LogInResult logInResult) {
        if (logInResult.isLogIn) {
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            holder.getActivity().startActivity(intent);
        }
        finish();
    }
}