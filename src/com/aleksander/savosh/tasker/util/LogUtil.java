package com.aleksander.savosh.tasker.util;


import android.util.Log;

public class LogUtil {

    public static void toLog(String alternativeMessage, Exception e){
        Log.e(LogUtil.class.getName(), e.getMessage() != null ? e.getMessage() : alternativeMessage);
        Log.d(LogUtil.class.getName(), e.getMessage() != null ? e.getMessage() : alternativeMessage, e);
    }

}
