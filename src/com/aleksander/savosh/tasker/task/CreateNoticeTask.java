package com.aleksander.savosh.tasker.task;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.service.NoticeService;
import com.aleksander.savosh.tasker.task.holder.NoticeTaskHolder;

import java.util.List;

public class CreateNoticeTask extends AbstractNoticeTask<List<Property>, Void, Boolean> {

    private static CreateNoticeTask currentTask;
    public static void initTask(List<Property> propertiesForCreate, NoticeTaskHolder holder, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new CreateNoticeTask();
                currentTask.holder = holder;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, propertiesForCreate);
            }
        } else {
            currentTask.holder = holder;
        }
    }

    private CreateNoticeTask(){}

    @Override
    protected Boolean doInBackground(List<Property>... params) {
        boolean wasException = false;
        startTask();
        try {
            Notice notice = new Notice(params[0]);
            NoticeService.createNotice(notice);
        } catch(Exception e){
            Log.e(getClass().getName(), e.getMessage());
            Log.d(getClass().getName(), e.getMessage(), e);
            wasException = true;
        }
        waitIfNeed();
        return wasException;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(!aBoolean){
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            holder.activity.startActivity(intent);
            holder.activity.finish();
        }
        super.onPostExecute(aBoolean);
        currentTask = null;
    }
}
