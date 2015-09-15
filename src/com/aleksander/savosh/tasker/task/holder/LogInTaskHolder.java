package com.aleksander.savosh.tasker.task.holder;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

//TODO написать Util class который будет разбирать Holder и делать разные елементы активными или неактивными, избавляемся от копипаст
public class LogInTaskHolder {

    public Activity activity;
    public EditText numberEditText;
    public EditText passwordEditText;
    public CheckBox rememberMeCheckBox;
    public Button logInButton;
    public Button signUpButton;
    public View progressBar;

}
