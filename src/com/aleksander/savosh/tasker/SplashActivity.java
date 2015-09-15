package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import com.aleksander.savosh.tasker.task.SynchronizeDataTask;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        //TODO нужно обезопасить AsyncTask
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                Application.instance().init();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                SynchronizeDataTask.initTask(SplashActivity.this, true);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
