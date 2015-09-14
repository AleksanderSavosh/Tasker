package com.aleksander.savosh.tasker.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.service.NoticeService;

import java.util.List;

public class CreateNoticeTask extends AsyncTask<List<Property>, Void, Boolean> {

    private static CreateNoticeTask currentTask;
    public static void initTask(List<Property> propertiesForCreate, Activity activity, Button button, ProgressBar progressBar, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new CreateNoticeTask();
                currentTask.activity = activity;
                currentTask.button = button;
                currentTask.progressBar = progressBar;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, propertiesForCreate);
            }
        } else {
            currentTask.activity = activity;
            currentTask.button = button;
            currentTask.progressBar = progressBar;
        }
    }

    private CreateNoticeTask(){}
    private Activity activity;
    private Button button;
    private ProgressBar progressBar;

    @Override
    protected void onPreExecute() {
        button.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(List<Property>... params) {
        try {
            Notice notice = new Notice(params[0]);
            NoticeService.createNotice(notice);
        } catch(Exception e){
            Log.e(getClass().getName(), e.getMessage());
            Log.d(getClass().getName(), e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        button.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if(aBoolean){
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
        activity = null;
        button = null;
        progressBar = null;
        currentTask = null;
    }
}
