package com.aleksander.savosh.tasker.task;

import android.os.AsyncTask;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.R;
import com.aleksander.savosh.tasker.util.LogUtil;
import com.aleksander.savosh.tasker.util.ViewUtil;

import java.util.Date;


public abstract class AbstractAsyncTask<Params, Progress, Result, Holder extends ComponentsHolder> extends AsyncTask<Params, Progress, Result> {

    protected Holder holder;
    protected Date startTask;

    @Override
    protected void onPreExecute() {
        ViewUtil.lock(holder.getViewsForLock());
        ViewUtil.showProgressBars(holder.getProgressBars());
        startTask = new Date();
    }

    public void setHolder(Holder holder){
        this.holder = holder;
    }

    protected void update(Holder holder){
        this.holder = holder;
        ViewUtil.lock(this.holder.getViewsForLock());
        ViewUtil.showProgressBars(this.holder.getProgressBars());
    }

    protected void waitIfNeed(){
        long taskTime = new Date().getTime() - startTask.getTime();
        long minTaskTime = Application.getContext().getResources().getInteger(R.integer.min_task_time);
        if(taskTime < minTaskTime){
            try {
                Thread.sleep(minTaskTime - taskTime);
            } catch (InterruptedException e) {
                LogUtil.toLog("exception wait if need", e);
            }
        }
    }

    protected void finish(){
        ViewUtil.unlock(holder.getViewsForLock());
        ViewUtil.hideProgressBars(holder.getProgressBars());
        holder = null;
    }
}
