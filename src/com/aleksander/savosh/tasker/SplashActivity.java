package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.aleksander.savosh.tasker.dao.CloudDao;
import com.aleksander.savosh.tasker.dao.LocalDao;
import com.aleksander.savosh.tasker.model.LogInData;
import com.aleksander.savosh.tasker.model.Notice;
import com.aleksander.savosh.tasker.model.Property;

import java.util.List;


public class SplashActivity extends Activity {

    public class SynchronizeDataTask extends AsyncTask <LogInData, Void, Void> {

        CloudDao<Notice> noticeCloudDao = Application.getNoticeCloudDao();
        CloudDao<Property> propertyCloudDao = Application.getPropertyCloudDao();
        LocalDao<Notice> noticeLocalDao = Application.getNoticeLocalDao();
        LocalDao<Property> propertyLocalDao = Application.getPropertyLocalDao();

        @Override
        protected Void doInBackground(LogInData... params) {
            LogInData logInData = params[0];

            try {
                List<Notice> localNotices = noticeLocalDao.readThrowExceptions(Notice.builder()
                        .setAccountId(logInData.getAccountId())
                        .build());
                List<Notice> cloudNotices = noticeCloudDao.readThrowExceptions(Notice.builder()
                        .setAccountId(logInData.getAccountId())
                        .build());

                //todo

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(Application.getContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
