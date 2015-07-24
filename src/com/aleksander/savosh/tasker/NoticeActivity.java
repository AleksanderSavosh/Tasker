package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.aleksander.savosh.tasker.model.*;

import java.util.*;

public class NoticeActivity extends Activity {

    public CreateNoticeTask createNoticeTask;
    public class CreateNoticeTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                LogInData logInData = Application.getLogInDataLocalDao().readFirstThrowExceptions(null);
                Notice notice = Application.getNoticeCloudDao()
                        .createThrowExceptions(Notice.builder().setAccountId(logInData.getAccountId()).build());
                Application.getNoticeLocalDao().createThrowExceptions(notice);

                Map<PropertyType, List<Property>> propertiesMap = NoticeActivity.this
                        .getMapOfPropertiesForNewNotice(notice.getObjectId());

                for(PropertyType type : propertiesMap.keySet()){
                    for(Property property : propertiesMap.get(type)){
                        property = Application.getPropertyCloudDao().createThrowExceptions(property);
                        Application.getPropertyLocalDao().createThrowExceptions(property);
                    }
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
                Log.d(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString(), e);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                Intent intent = new Intent(Application.getContext(), MainActivity.class);
                NoticeActivity.this.startActivity(intent);
                NoticeActivity.this.finish();
            }
            createNoticeTask = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_activity);

        final Notice notice = savedInstanceState != null && savedInstanceState.containsKey("notice") ?
                (Notice) savedInstanceState.getSerializable("notice") : null;

        Button save = (Button) findViewById(R.id.notice_activity_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(notice == null){
                    if(createNoticeTask == null){
                        createNoticeTask = new CreateNoticeTask();
                        createNoticeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }

            }
        });
    }


    private Map<PropertyType, List<Property>> getMapOfPropertiesForNewNotice(String noticeId){
        Map<PropertyType, List<Property>> map = new HashMap<PropertyType, List<Property>>();
        List<PropertyType> propertyTypes = Arrays.asList(
                PropertyType.TITLE, PropertyType.CREATE_DATE, PropertyType.TEXT);
        for(PropertyType type : propertyTypes){
            map.put(type, getProperties(type, noticeId));
        }
        return map;
    }

    private List<Property> getProperties(PropertyType propertyType, String noticeId){
        List<Property> properties = new ArrayList<Property>();
        switch(propertyType) {
            case TITLE: {
                EditText editText = (EditText) findViewById(R.id.notice_activity_title);
                if(editText.getVisibility() == View.VISIBLE){
                    properties.add(Property.builder()
                            .setNoticeId(noticeId)
                            .setType(propertyType)
                            .setText(editText.getText().toString())
                            .build());
                }
                break;
            }
            case TEXT: {
                EditText editText = (EditText) findViewById(R.id.notice_activity_text);
                if(editText.getVisibility() == View.VISIBLE){
                    properties.add(Property.builder()
                            .setNoticeId(noticeId)
                            .setType(propertyType)
                            .setText(editText.getText().toString())
                            .build());
                }
                break;
            }
            case CREATE_DATE: {
                properties.add(Property.builder()
                        .setNoticeId(noticeId)
                        .setType(propertyType)
                        .setDate(new Date())
                        .build());
                break;
            }
        }

        return properties;
    }

}

