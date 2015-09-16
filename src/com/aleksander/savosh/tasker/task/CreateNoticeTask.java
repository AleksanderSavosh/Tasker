package com.aleksander.savosh.tasker.task;

import android.content.Intent;
import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.service.NoticeService;
import com.aleksander.savosh.tasker.task.holder.NoticeTaskHolder;

import java.util.List;

public class CreateNoticeTask extends AbstractAsyncTask<List<Property>, Void, Boolean, NoticeTaskHolder> {

    @Override
    protected Boolean doInBackground(List<Property>... params) {
        boolean wasException = false;
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
            holder.getActivity().startActivity(intent);
            holder.getActivity().finish();
        }
        finish();
    }
}
