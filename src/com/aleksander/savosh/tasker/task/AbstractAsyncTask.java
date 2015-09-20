package com.aleksander.savosh.tasker.task;

import android.os.AsyncTask;
import android.util.Log;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.R;
import com.aleksander.savosh.tasker.task.holder.ComponentsHolder;
import com.aleksander.savosh.tasker.util.LogUtil;
import com.aleksander.savosh.tasker.util.ViewUtil;

import java.util.Date;


public abstract class AbstractAsyncTask<Params, Progress, Result, Holder extends ComponentsHolder> extends AsyncTask<Params, Progress, Result> {

    protected Holder holder;
    protected Date startTask;

    @Override
    protected void onPreExecute() {
        ViewUtil.lock(holder.getViewsForLock());
//        ViewUtil.hideViews(holder.getViewsForHide());
        ViewUtil.showViews(holder.getProgressBars());
        Log.d(getClass().getName(), "SHOW PROGRESS BAR");
        startTask = new Date();
    }

    public void setHolder(Holder holder){
        this.holder = holder;
    }

    protected void update(Holder holder){
        this.holder = holder;
        ViewUtil.lock(this.holder.getViewsForLock());
        ViewUtil.showViews(this.holder.getProgressBars());
        Log.d(getClass().getName(), "SHOW PROGRESS BAR");
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
        ViewUtil.showViews(holder.getViewsForHide());
        ViewUtil.hideViews(holder.getProgressBars());
        Log.d(getClass().getName(), "HIDE PROGRESS BAR");
        holder = null;
    }
}
