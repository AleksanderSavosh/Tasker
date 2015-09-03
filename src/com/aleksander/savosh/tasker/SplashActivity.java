package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.service.SynchronizeService;
import java.util.Map;


public class SplashActivity extends Activity {

    public class SynchronizeDataTask extends AsyncTask <Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Map<String, Account> accounts = ((Application) getApplicationContext()).getAccounts();
                for(Account acc : accounts.values()) {
                    SynchronizeService.synchronizeLocalWithCloud(acc);
                }

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
        new SynchronizeDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
