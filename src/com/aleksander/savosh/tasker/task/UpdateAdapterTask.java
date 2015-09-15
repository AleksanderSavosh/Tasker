package com.aleksander.savosh.tasker.task;


import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class UpdateAdapterTask extends AsyncTask<Void, Void, Collection<Notice>> {

    private static UpdateAdapterTask currentTask;
    public static void initTask(ArrayAdapter adapter, ListView listView, View progressBar, boolean createAndExecute) {
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
    private View progressBar;

    private Date taskStart;

    @Override
    protected void onPreExecute() {
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Collection<Notice> doInBackground(Void... params) {
        taskStart = new Date();
        Collection<Notice> result = new ArrayList<Notice>();
        try {
            Config config = Application.instance().getConfig();
            String accountId = StringUtil.isEmpty(config.accountId) ? Config.ACC_ZERO : config.accountId;
            Account account = Application.instance().getAccounts().get(accountId);
            Log.d(getClass().getName(), "Current notices count: " + account.getNotices().size());
            result = account.getNotices();
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
            Log.d(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString(), e);
        }

        long taskTime = new Date().getTime() - taskStart.getTime();
        if(taskTime < 5000){ // если время выполнения меньше 5 сек
            try {
                Thread.sleep(5000 - taskTime);
            } catch (InterruptedException e) {
                Log.e(getClass().getName(), e != null ? e.getMessage() : "Error in doInBackground");
                Log.d(getClass().getName(), e != null ? e.getMessage() : "Error in doInBackground", e);
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Collection<Notice> notices) {
        if(!notices.isEmpty()) {
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
