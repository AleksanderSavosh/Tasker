package com.aleksander.savosh.tasker.task.holder;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class LogInTaskHolder implements ComponentsHolder {

    public Activity activity;
    public EditText numberEditText;
    public EditText passwordEditText;
    public CheckBox rememberMeCheckBox;
    public Button logInButton;
    public Button signUpButton;
    public View progressBar;

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public List<View> getViewsForLock() {
        return new ArrayList<View>(){{
            add(numberEditText);
            add(passwordEditText);
            add(rememberMeCheckBox);
            add(logInButton);
            add(signUpButton);
        }};
    }

    @Override
    public List<View> getProgressBars() {
        return new ArrayList<View>(){{
            add(progressBar);
        }};
    }

    @Override
    public List<View> getViewsForHide() {
        return new ArrayList<View>();
    }
}
