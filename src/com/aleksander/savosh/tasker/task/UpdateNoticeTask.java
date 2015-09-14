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
import com.aleksander.savosh.tasker.service.NoticeService;


public class UpdateNoticeTask extends AsyncTask<Notice, Void, Boolean> {

    private static UpdateNoticeTask currentTask;

    public static void initTask(Notice noticeForUpdate, Activity activity, Button button, ProgressBar progressBar, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new UpdateNoticeTask();
                currentTask.activity = activity;
                currentTask.button = button;
                currentTask.progressBar = progressBar;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, noticeForUpdate);
            }
        } else {
            currentTask.activity = activity;
            currentTask.button = button;
            currentTask.progressBar = progressBar;
        }
    }

    private UpdateNoticeTask() {}
    private Activity activity;
    private Button button;
    private ProgressBar progressBar;

    @Override
    protected void onPreExecute() {
        button.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Boolean doInBackground(Notice... params) {
        try {
            NoticeService.updateNotice(params[0]);
        } catch (Exception e) {
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
        if (aBoolean) {
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
        button = null;
        progressBar = null;
        activity = null;
        currentTask = null;
    }
}