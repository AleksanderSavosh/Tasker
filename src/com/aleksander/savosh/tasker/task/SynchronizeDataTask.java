package com.aleksander.savosh.tasker.task;

import android.content.Intent;
import android.widget.Toast;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.R;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.service.SynchronizeService;
import com.aleksander.savosh.tasker.task.holder.SynchronizeTaskHolder;
import com.aleksander.savosh.tasker.util.LogUtil;
import com.aleksander.savosh.tasker.util.StringUtil;


public class SynchronizeDataTask extends AbstractAsyncTask<Void, Void, Boolean, SynchronizeTaskHolder> {

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean wasException = false;
        try {
            Config config = Application.instance().getConfig();
            if(!StringUtil.isEmpty(config.accountId)){
                SynchronizeService.synchronizeLocalWithCloud(config.accountId);
            }
        } catch (Exception e) {
            LogUtil.toLog("Error in doInBackground synch task", e);
            wasException = true;
        }
        waitIfNeed();
        return wasException;
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
        holder.getActivity().startActivity(intent);
        holder.getActivity().finish();
        finish();
    }
}