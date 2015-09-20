package com.aleksander.savosh.tasker.task.holder;


import android.app.Activity;
import android.view.View;

import java.util.List;

public interface ComponentsHolder {

    public Activity getActivity();
    public List<View> getViewsForLock();
    public List<View> getProgressBars();
    public List<View> getViewsForHide();

}
