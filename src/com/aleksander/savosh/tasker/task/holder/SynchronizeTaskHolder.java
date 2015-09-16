package com.aleksander.savosh.tasker.task.holder;

import android.app.Activity;
import android.view.View;
import com.aleksander.savosh.tasker.task.ComponentsHolder;

import java.util.ArrayList;
import java.util.List;


public class SynchronizeTaskHolder implements ComponentsHolder {

    public Activity activity;

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public List<View> getViewsForLock() {
        return new ArrayList<View>();
    }

    @Override
    public List<View> getProgressBars() {
        return new ArrayList<View>();
    }
}
