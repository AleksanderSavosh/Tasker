package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import com.aleksander.savosh.tasker.task.SynchronizeDataTask;
import com.aleksander.savosh.tasker.task.holder.SynchronizeTaskHolder;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        final SynchronizeTaskHolder holder = new SynchronizeTaskHolder(){{
            activity = SplashActivity.this;
        }};

        Application.getAsyncTaskManager().updateTask(SynchronizeDataTask.class, holder);

        //TODO нужно обезопасить AsyncTask
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                Application.instance().init();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                Application.getAsyncTaskManager().<SynchronizeTaskHolder, Void>startTask(SynchronizeDataTask.class, holder);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
