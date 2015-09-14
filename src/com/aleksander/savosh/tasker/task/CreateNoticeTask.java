package com.aleksander.savosh.tasker.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.service.NoticeService;

import java.util.List;

public class CreateNoticeTask extends AsyncTask<List<Property>, Void, Boolean> {

    private static CreateNoticeTask currentTask;
    public static void initTask(List<Property> propertiesForCreate, Activity activity, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new CreateNoticeTask();
                currentTask.activity = activity;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, propertiesForCreate);
            }
        } else {
            currentTask.activity = activity;
        }
    }

    private CreateNoticeTask(){}
    private Activity activity;

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
        if(aBoolean){
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
        activity = null;
        currentTask = null;
    }
}
