package com.aleksander.savosh.tasker.util;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.method.KeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.aleksander.savosh.tasker.view.ColorfulProgressBar;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewUtil {

    public static final int KEY_LISTENER = 1;

    public static void lock(View view){
        view.setClickable(false);

        if(view instanceof EditText) {
            KeyListener keyListener = ((EditText) view).getKeyListener();
            view.setTag(keyListener);
        }
    }

    public static void unlock(View view){
        view.setClickable(true);

        if(view instanceof EditText) {
            KeyListener keyListener = (KeyListener) view.getTag();
            ((EditText) view).setKeyListener(keyListener);
        }
    }

    public static void lock(List<View> views){
        for(View view : views){
            lock(view);
        }
    }

    public static void unlock(List<View> views){
        for(View view : views){
            unlock(view);
        }
    }

    public static void lock(Object holder){
        Set<Field> fields = ReflectionUtil.getFields(holder, new HashSet<Class>(){{
            add(EditText.class);
            add(Button.class);
        }});
        for(Field field : fields){
            try {
                lock((View) field.get(holder));
            } catch (IllegalAccessException e) {
                LogUtil.toLog("lock exception", e);
            }
        }
    }

    public static void unlock(Object holder){
        Set<Field> fields = ReflectionUtil.getFields(holder, new HashSet<Class>(){{
            add(EditText.class);
            add(Button.class);
        }});
        for(Field field : fields){
            try {
                unlock((View) field.get(holder));
            } catch (IllegalAccessException e) {
                LogUtil.toLog("unlock exception", e);
            }
        }
    }


    public static void showViews(List<View> views){
        for(View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(List<View> views){
        for(View view : views) {
            view.setVisibility(View.GONE);
        }
    }
}
