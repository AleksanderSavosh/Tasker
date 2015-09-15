package com.aleksander.savosh.tasker.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.data.SignUpData;
import com.aleksander.savosh.tasker.data.SignUpResult;
import com.aleksander.savosh.tasker.service.SingUpLogInLogOutService;

//TODO переписать класс с использование ViewHolder, избавляемся от лишних рапаметров в методе initTask
public class SignUpTask extends AsyncTask<SignUpData, Void, SignUpResult> {

    private static SignUpTask currentTask;
    public static void initTask(SignUpData signUpData, Activity activity, TextView textViewMessage, boolean createAndExecute){
        if(currentTask == null){
            if(createAndExecute) {
                currentTask = new SignUpTask();
                currentTask.activity = activity;
                currentTask.textViewMessage = textViewMessage;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, signUpData);
            }
        } else {
            currentTask.activity = activity;
            currentTask.textViewMessage = textViewMessage;
        }
    }

    //components
    private Activity activity;
    private TextView textViewMessage;

    //singleton
    private SignUpTask(){}

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
            activity.startActivity(intent);
            activity.finish();
        } else {
            textViewMessage.setVisibility(View.VISIBLE);
            textViewMessage.setText(result.message);
        }
        activity = null;
        textViewMessage = null;
        currentTask = null;
    }
}
