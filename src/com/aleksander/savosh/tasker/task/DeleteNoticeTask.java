package com.aleksander.savosh.tasker.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.service.NoticeService;


public class DeleteNoticeTask extends AsyncTask<Notice, Void, Boolean> {

    private static DeleteNoticeTask currentTask;

    public static void initTask(Notice noticeForDelete, Activity activity, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new DeleteNoticeTask();
                currentTask.activity = activity;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, noticeForDelete);
            }
        } else {
            currentTask.activity = activity;
        }
    }

    private DeleteNoticeTask() {}
    private Activity activity;

    @Override
    protected Boolean doInBackground(Notice... params) {
        try {
            NoticeService.deleteNotice(params[0].getObjectId());
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