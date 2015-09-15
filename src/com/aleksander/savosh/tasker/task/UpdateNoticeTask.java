package com.aleksander.savosh.tasker.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.service.NoticeService;
import com.aleksander.savosh.tasker.task.holder.ComponentsHolder;


public class UpdateNoticeTask extends AbstractNoticeTask<Notice, Void, Boolean> {

    private static UpdateNoticeTask currentTask;

    public static void initTask(Notice noticeForUpdate, ComponentsHolder holder, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new UpdateNoticeTask();
                currentTask.holder = holder;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, noticeForUpdate);
            }
        } else {
            currentTask.holder = holder;
        }
    }

    private UpdateNoticeTask() {}

    @Override
    protected Boolean doInBackground(Notice... params) {
        boolean wasException = false;
        startTask();
        try {
            NoticeService.updateNotice(params[0]);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
            Log.d(getClass().getName(), e.getMessage(), e);
            wasException = true;
        }
        waitIfNeed();
        return wasException;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (!aBoolean) {
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            holder.activity.startActivity(intent);
            holder.activity.finish();
        }
        super.onPostExecute(aBoolean);
        currentTask = null;
    }
}