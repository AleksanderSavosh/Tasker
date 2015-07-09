package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.aleksander.savosh.tasker.model.Phone;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign__up_activity);

        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        Log.i(getClass().getName(),"Number 1: " + tMgr.getLine1Number());
        Log.i(getClass().getName(),"Device id: " + tMgr.getDeviceId());

        final EditText phoneEditText = (EditText) findViewById(R.id.sign_up_activity_phone);
        final EditText passwordEditText = (EditText) findViewById(R.id.sign_up_activity_password);
        final EditText repeatPasswordEditText = (EditText) findViewById(R.id.sign_up_activity_repeat_password);
        final TextView message = (TextView) findViewById(R.id.sign_up_activity_message);

        findViewById(R.id.sign_up_activity_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //проверка на правильный формат телефона
                //проверка на совпадение паролей
                //проверка что такого аккаунта еще нет
                //запись данных в облако
                //запись данных для входа в локальное хранилище
                //переход в меню

                if(!passwordEditText.getText().toString().equals(repeatPasswordEditText.getText().toString())){
                    message.setText(R.string.passwords_doesnt_equal);
                    return;
                }

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Phone");
                query.whereEqualTo("number", phoneEditText.getText().toString());
                try {
                    query.getFirst();
                } catch (ParseException e) {
                    String ex = "no results found for query";
                    e.printStackTrace();
                }

            }
        });
    }
}
