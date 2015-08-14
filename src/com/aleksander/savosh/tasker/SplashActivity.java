package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.aleksander.savosh.tasker.model.relational.LogInData;
import com.aleksander.savosh.tasker.service.SynchronizeService;


public class SplashActivity extends Activity {

    public class SynchronizeDataTask extends AsyncTask <LogInData, Void, Boolean> {

        @Override
        protected Boolean doInBackground(LogInData... params) {
            LogInData logInData = params[0];

            try {
                SynchronizeService.updateLocalByCloud(logInData.getAccountId());
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage());
                Log.d(getClass().getName(), e.getMessage(), e);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean wasException) {
            if(wasException){
                Toast.makeText(
                        Application.getContext(),
                        getResources().getText(R.string.update_local_data_exception),
                        Toast.LENGTH_LONG)
                        .show();
            }
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogInData logInData = Application.getLogInDataLocalDao().readFirst(null);
        if(logInData == null || logInData.getRememberMe() == null || !logInData.getRememberMe()){
            Application.getLogInDataLocalDao().delete(logInData);
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            //run synchronization task and after this go to main activity
            new SynchronizeDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, logInData);
        }
    }
}
