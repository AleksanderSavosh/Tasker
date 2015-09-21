package com.aleksander.savosh.tasker.task.holder;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.aleksander.savosh.tasker.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class UpdateAdapterTaskHolder implements ComponentsHolder {

    public MainActivity.RecyclerViewAdapter recyclerViewAdapter;
    public View progressBar;

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public List<View> getViewsForLock() {
        return new ArrayList<View>();
    }

    @Override
    public List<View> getProgressBars() {
        return new ArrayList<View>();
    }

    @Override
    public List<View> getViewsForHide() {
        return new ArrayList<View>(){{
        }};
    }
}
