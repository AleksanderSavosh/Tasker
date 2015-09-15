package com.aleksander.savosh.tasker.task;

import android.os.AsyncTask;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import com.aleksander.savosh.tasker.task.holder.NoticeTaskHolder;

import java.util.Date;

public abstract class AbstractNoticeTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> {

    protected NoticeTaskHolder holder;

    //TODO так как локи не сохраняются при перевороте экрана, доработать AsyncTasks
    @Override
    protected void onPreExecute() {
        holder.progressBar.setVisibility(View.VISIBLE);

        //запрет редактирования
        holder.button.setClickable(false);
        holder.titleEditText.setTag(holder.titleEditText.getKeyListener());
        holder.titleEditText.setKeyListener(null);
        holder.textEditText.setTag(holder.textEditText.getKeyListener());
        holder.textEditText.setKeyListener(null);
    }


    private Date startTask;
    protected void startTask(){
        startTask = new Date();
    }

    protected void waitIfNeed(){
        long taskTime = new Date().getTime() - startTask.getTime();
        if(taskTime < 5000){ //TODO переписать 5000 милисек на использование константы
            try {
                Thread.sleep(5000 - taskTime);
            } catch (InterruptedException e) {
                Log.e(getClass().getName(), e != null ? e.getMessage() : "Error in doInBackground");
                Log.d(getClass().getName(), e != null ? e.getMessage() : "Error in doInBackground", e);
            }
        }
    }

    @Override
    protected void onPostExecute(Result aBoolean) {
        holder.progressBar.setVisibility(View.GONE);

        // resume editable
        holder.button.setClickable(true);
        holder.titleEditText.setKeyListener((KeyListener) holder.titleEditText.getTag());
        holder.textEditText.setKeyListener((KeyListener) holder.textEditText.getTag());

        holder = null;
    }


}
