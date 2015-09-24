package com.aleksander.savosh.tasker;


import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import com.aleksander.savosh.tasker.util.ActivityUtil;

public class SettingsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityUtil.setTheme(this);
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 11){
            getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment() {
                @Override
                public void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    addPreferencesFromResource(R.xml.preferences);
                }
            }).commit();
        } else {
//            addPreferencesFromResource(R.xml.preferences);
        }

    }

}
