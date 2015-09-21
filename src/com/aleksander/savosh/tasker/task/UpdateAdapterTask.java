package com.aleksander.savosh.tasker.task;

import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.task.holder.UpdateAdapterTaskHolder;
import com.aleksander.savosh.tasker.util.LogUtil;
import com.aleksander.savosh.tasker.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;


public class UpdateAdapterTask extends AbstractAsyncTask<Void, Void, Collection<Notice>, UpdateAdapterTaskHolder> {

    @Override
    protected Collection<Notice> doInBackground(Void... params) {
        Collection<Notice> result = new ArrayList<Notice>();
        try {
            Config config = Application.instance().getConfig();
            String accountId = StringUtil.isEmpty(config.accountId) ? Config.ACC_ZERO : config.accountId;
            Account account = Application.instance().getAccounts().get(accountId);
            Log.d(getClass().getName(), "Current notices count: " + account.getNotices().size());
            result = account.getNotices();
        } catch (Exception e) {
            LogUtil.toLog("Error in update adapter task", e);
        }

        waitIfNeed();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Collection<Notice> notices) {
        if(!notices.isEmpty()) {

            holder.recyclerViewAdapter.addAll(notices);
            holder.recyclerViewAdapter.notifyDataSetChanged();
        }
        finish();
    }
}
