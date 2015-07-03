package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.aleksander.savosh.tasker.model.Phone;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class LoginActivity extends Activity {

    private EditText number;
    private TextView message;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.login_activity_log_in) {
                message.setVisibility(View.VISIBLE);
                message.setText(LoginActivity.this.getResources().getText(R.string.wait));

                Log.i(getClass().getName(), "Find number: " + number.getText().toString());
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Phone");
                query.whereEqualTo("number", number.getText().toString());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            Phone phone = Phone.fromParseObject(object);
                            Log.i(getClass().getName(), phone.toString());
                            //
                        } else {
                            if ("no results found for query".equalsIgnoreCase(e.getMessage())){
                                message.setText(
                                        LoginActivity.this.getResources().getText(R.string.number_or_password_is_invalid)
                                );
                            }
                            Log.d(getClass().getName(), e.getMessage(), e);
                            Log.e(getClass().getName(), e.getMessage());
                            message.setText(e.getMessage());
                        }
                    }
                });

            } else if (v.getId() == R.id.login_activity_sign_up) {
                //sign up
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        number = (EditText) findViewById(R.id.login_activity_phone_number);
        message = (TextView) findViewById(R.id.login_activity_message);

        findViewById(R.id.login_activity_log_in).setOnClickListener(clickListener);
        findViewById(R.id.login_activity_sign_up).setOnClickListener(clickListener);
    }
}