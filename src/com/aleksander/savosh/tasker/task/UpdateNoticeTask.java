package com.aleksander.savosh.tasker.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.service.NoticeService;
import com.aleksander.savosh.tasker.task.holder.NoticeTaskHolder;


public class UpdateNoticeTask extends AbstractAsyncTask<Notice, Void, Boolean, NoticeTaskHolder> {

    @Override
    protected Boolean doInBackground(Notice... params) {
        boolean wasException = false;
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
        finish();
    }
}