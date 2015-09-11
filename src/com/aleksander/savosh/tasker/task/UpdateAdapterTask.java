package com.aleksander.savosh.tasker.task;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.util.StringUtil;

import java.util.Collection;

public class UpdateAdapterTask extends AsyncTask<Void, Void, Collection<Notice>> {

    private static UpdateAdapterTask currentTask;
    public static void initTask(ArrayAdapter adapter, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new UpdateAdapterTask();
                currentTask.adapter = adapter;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            currentTask.adapter = adapter;
        }
    }

    private UpdateAdapterTask(){}
    private ArrayAdapter adapter;

    @Override
    protected Collection<Notice> doInBackground(Void... params) {
        try {
            Config config = Application.instance().getConfig();
            String accountId = StringUtil.isEmpty(config.accountId) ? Config.ACC_ZERO : config.accountId;
            Account account = Application.instance().getAccounts().get(accountId);
            Log.d(getClass().getName(), "Current notices count: " + account.getNotices().size());
            return  account.getNotices();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
            Log.d(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString(), e);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Collection<Notice> notices) {
        if(notices != null) {
            adapter.clear();
            adapter.addAll(notices);
            adapter.notifyDataSetChanged();
        }
        adapter = null;
        currentTask = null;
    }
}
