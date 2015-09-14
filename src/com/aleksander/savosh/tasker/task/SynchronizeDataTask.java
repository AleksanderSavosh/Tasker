package com.aleksander.savosh.tasker.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.R;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.service.SynchronizeService;
import com.aleksander.savosh.tasker.util.StringUtil;

import java.util.Map;


public class SynchronizeDataTask extends AsyncTask<Void, Void, Boolean> {

    private static SynchronizeDataTask currentTask;
    public static void initTask(Activity activity, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new SynchronizeDataTask();
                currentTask.activity = activity;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            currentTask.activity = activity;
        }
    }


    private SynchronizeDataTask(){}
    private Activity activity;

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Config config = Application.instance().getConfig();
            if(!StringUtil.isEmpty(config.accountId)){
                SynchronizeService.synchronizeLocalWithCloud(config.accountId);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e != null ? e.getMessage() : "Error in doInBackground");
            Log.d(getClass().getName(), e != null ? e.getMessage() : "Error in doInBackground", e);
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean wasException) {
        if(wasException){
            Toast.makeText(
                    Application.getContext(),
                    Application.getContext().getResources().getText(R.string.update_local_data_exception),
                    Toast.LENGTH_LONG)
                    .show();
        }
        Intent intent = new Intent(Application.getContext(), MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
        activity = null;
        currentTask = null;
    }
}