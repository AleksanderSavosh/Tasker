package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.aleksander.savosh.tasker.task.SynchronizeDataTask;
import com.aleksander.savosh.tasker.task.holder.SynchronizeTaskHolder;
import com.aleksander.savosh.tasker.util.LogUtil;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "--- === ON CREATE SPLASH ACTIVITY === ---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        final SynchronizeTaskHolder holder = new SynchronizeTaskHolder(){{
            activity = SplashActivity.this;
        }};

        Application.getAsyncTaskManager().updateTask(SynchronizeDataTask.class, holder);

        //TODO нужно обезопасить AsyncTask
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Application.instance().init();
                } catch(Exception e){
                    LogUtil.toLog("Exception", e);
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                Application.getAsyncTaskManager().<SynchronizeTaskHolder, Void>startTask(SynchronizeDataTask.class, holder);
            }
        };

        if(Build.VERSION.SDK_INT >= 11) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncTask.execute();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getName(), "--- === ON DESTROY SPLASH ACTIVITY === ---");
        super.onDestroy();
    }
}
