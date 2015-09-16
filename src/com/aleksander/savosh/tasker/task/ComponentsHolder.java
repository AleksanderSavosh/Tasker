package com.aleksander.savosh.tasker.task;


import android.app.Activity;
import android.view.View;

import java.util.List;

public interface ComponentsHolder {

    public Activity getActivity();
    public List<View> getViewsForLock();
    public List<View> getProgressBars();

}
