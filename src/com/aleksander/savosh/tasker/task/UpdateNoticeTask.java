package com.aleksander.savosh.tasker.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.service.NoticeService;


public class UpdateNoticeTask extends AsyncTask<Notice, Void, Boolean> {

    private static UpdateNoticeTask currentTask;

    public static void initTask(Notice noticeForUpdate, Activity activity, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new UpdateNoticeTask();
                currentTask.activity = activity;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, noticeForUpdate);
            }
        } else {
            currentTask.activity = activity;
        }
    }

    private UpdateNoticeTask() {}
    private Activity activity;

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
        if (aBoolean) {
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
        activity = null;
        currentTask = null;
    }
}