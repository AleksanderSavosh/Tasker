package com.aleksander.savosh.tasker.task.holder;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.aleksander.savosh.tasker.task.ComponentsHolder;

import java.util.ArrayList;
import java.util.List;

public class NoticeTaskHolder implements ComponentsHolder {
    public Activity activity;
    public EditText titleEditText;
    public EditText textEditText;
    public Button button;
    public View progressBar;

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public List<View> getViewsForLock() {
        return new ArrayList<View>(){{
            add(titleEditText);
            add(textEditText);
            add(button);
        }};
    }

    @Override
    public List<View> getProgressBars() {
        return new ArrayList<View>(){{
            add(progressBar);
        }};
    }
}
