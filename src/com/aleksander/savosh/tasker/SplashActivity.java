package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.os.Bundle;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            ParseObject object = ParseObject.create("Test");
            object.put("Savosh", "Test");
            object.save();

            System.out.println(object.getObjectId());
            System.out.println(object.getCreatedAt());
            System.out.println(object.getUpdatedAt());


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
