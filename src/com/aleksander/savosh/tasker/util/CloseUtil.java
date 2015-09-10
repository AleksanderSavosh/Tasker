package com.aleksander.savosh.tasker.util;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;


public class CloseUtil {
    public static void closeNoThrowException(Closeable... closeables){
        for(Closeable closeable : closeables){
            try {
                if(closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                Log.e(CloseUtil.class.getName(), e.getMessage());
                Log.d(CloseUtil.class.getName(), e.getMessage(), e);
            }
        }
    }
}
