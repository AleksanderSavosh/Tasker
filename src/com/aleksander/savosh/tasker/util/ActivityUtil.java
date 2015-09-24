package com.aleksander.savosh.tasker.util;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import com.aleksander.savosh.tasker.MainActivity;
import com.aleksander.savosh.tasker.R;

public class ActivityUtil {

    public static void setTheme(Activity activity){
        Log.d(ActivityUtil.class.getName(), "--- === SET DEFAULT PREFERENCES === ---");
        PreferenceManager.setDefaultValues(activity, R.xml.preferences, false);

        String themeName = PreferenceManager.getDefaultSharedPreferences(activity).getString("pref_list_themes", "none");
        Log.d(ActivityUtil.class.getName(), "THEME: " + themeName);
        if(themeName.equals(activity.getResources().getString(R.string.theme_light))){
            activity.setTheme(R.style.Custom_Light);
        } else if(themeName.equals(activity.getResources().getString(R.string.theme_dark))){
            activity.setTheme(R.style.Custom_Dark);
        } else {
            activity.setTheme(R.style.Custom_Light);// if no theme found
        }
    }

    public static void checkIfThemeChanged(Activity activity) {
//        activity.getTheme();


    }
}
