package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.aleksander.savosh.tasker.model.LogInData;


public class SplashActivity extends Activity {

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

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
