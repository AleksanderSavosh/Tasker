package com.aleksander.savosh.tasker.task;


import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;
import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.task.holder.ComponentsHolder;
import com.aleksander.savosh.tasker.util.LogUtil;


public class AsyncTaskManager {

    private AbstractAsyncTask absAsyncTask;

    public <Holder extends ComponentsHolder> void updateTask(Class asyncTaskClass, Holder holder){
        if(absAsyncTask != null && asyncTaskClass.equals(absAsyncTask.getClass()) && absAsyncTask.getStatus() != AsyncTask.Status.FINISHED){
            absAsyncTask.update(holder);
        }
    }

    public  <Holder extends ComponentsHolder, Param>  void startTask(Class asyncTaskClass, Holder holder, Param... params){
        if(absAsyncTask == null || absAsyncTask.getStatus() == AsyncTask.Status.FINISHED){

            try {

                absAsyncTask = (AbstractAsyncTask) asyncTaskClass.getConstructor().newInstance();
                absAsyncTask.setHolder(holder);
                if(Build.VERSION.SDK_INT >= 11) {
                    absAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
                } else {
                    absAsyncTask.execute(params);
                }

            } catch (Exception e) {
                LogUtil.toLog("Create task exception", e);
            }

        } else {
            Toast.makeText(Application.getContext(), "Wait for finish last operation", Toast.LENGTH_LONG).show();
        }
    }


}
