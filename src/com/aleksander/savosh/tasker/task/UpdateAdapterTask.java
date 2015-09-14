package com.aleksander.savosh.tasker.task;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.util.StringUtil;

import java.util.Collection;

public class UpdateAdapterTask extends AsyncTask<Void, Void, Collection<Notice>> {

    private static UpdateAdapterTask currentTask;
    public static void initTask(ArrayAdapter adapter, ListView listView, ProgressBar progressBar, boolean createAndExecute) {
        if (currentTask == null) {
            if (createAndExecute) {
                currentTask = new UpdateAdapterTask();
                currentTask.adapter = adapter;
                currentTask.listView = listView;
                currentTask.progressBar = progressBar;
                currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            currentTask.adapter = adapter;
            currentTask.listView = listView;
            currentTask.progressBar = progressBar;
        }
    }

    private UpdateAdapterTask(){}
    private ArrayAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    protected void onPreExecute() {
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

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
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        adapter = null;
        listView = null;
        progressBar = null;
        currentTask = null;
    }
}
