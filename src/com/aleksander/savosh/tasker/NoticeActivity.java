package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.aleksander.savosh.tasker.dao.relational.LocalDao;
import com.aleksander.savosh.tasker.model.relational.*;
import com.aleksander.savosh.tasker.service.NoticeService;

import java.util.*;

public class NoticeActivity extends Activity {

    public UpdateNoticeTask updateNoticeTask;
    public class UpdateNoticeTask extends AsyncTask<NoticeWithProperties, Void, Boolean> {
        @Override
        protected Boolean doInBackground(NoticeWithProperties... params) {
            try {
                NoticeWithProperties oldNoticeWithProperties = params[0];

                Map<Integer, List<Property>> newPropertiesMap = NoticeActivity.this
                        .getNewMapOfProperties(oldNoticeWithProperties.getNotice().getObjectId());

                Set<Integer> newNoticeTypes = new HashSet<Integer>();
                Set<Integer> commonNoticeTypes = new HashSet<Integer>();
                Set<Integer> oldNoticeTypes = new HashSet<Integer>();

                newNoticeTypes.addAll(newPropertiesMap.keySet());
                oldNoticeTypes.addAll(oldNoticeWithProperties.getPropertiesMap().keySet());

                newNoticeTypes.remove(PropertyType.CREATE_DATE);
                oldNoticeTypes.remove(PropertyType.CREATE_DATE);

                Set<Integer> allNoticeTypes = new HashSet<Integer>();
                allNoticeTypes.addAll(newNoticeTypes);
                allNoticeTypes.addAll(oldNoticeTypes);

                for(Integer key : allNoticeTypes){
                    if(newNoticeTypes.contains(key) && oldNoticeTypes.contains(key)){
                        commonNoticeTypes.add(key);
                        newNoticeTypes.remove(key);
                        oldNoticeTypes.remove(key);
                    }
                }

                LocalDao<Property> propertyLocalDao = Application.getPropertyLocalDao();

                for(Integer key : newNoticeTypes){
                    for(Property property : newPropertiesMap.get(key)){
                        propertyLocalDao.createThrowExceptions(property);
                    }
                }

                for(Integer key : oldNoticeTypes){
                    for(Property property : oldNoticeWithProperties.getPropertiesMap().get(key)){
                        propertyLocalDao.deleteThrowExceptions(property);
                    }
                }

                for(Integer key : commonNoticeTypes){
                    List<Property> newProperties = newPropertiesMap.get(key);
                    List<Property> oldProperties = oldNoticeWithProperties.getPropertiesMap().get(key);

                    for(Property property : oldProperties){
                        propertyLocalDao.deleteThrowExceptions(property);
                    }

                    for(Property property : newProperties){
                        propertyLocalDao.createThrowExceptions(property);
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
            updateNoticeTask = null;
        }
    }

    public CreateNoticeTask createNoticeTask;
    public class CreateNoticeTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                LocalDao<LogInData> logInDataLocalDao = Application.getLogInDataLocalDao();
                LocalDao<Notice> noticeLocalDao = Application.getNoticeLocalDao();
                LocalDao<Property> propertyLocalDao = Application.getPropertyLocalDao();

                LogInData logInData = logInDataLocalDao.readFirst(null);

                Notice notice = noticeLocalDao.createThrowExceptions(Notice.builder()
                        .setAccountId(logInData == null ? NoticeService.DEFAULT_ACCOUNT_ID : logInData.getAccountId()).build());

                Map<Integer, List<Property>> propertiesMap = NoticeActivity.this
                        .getNewMapOfProperties(notice.getObjectId());

                for(Integer type : propertiesMap.keySet()){
                    for(Property property : propertiesMap.get(type)){
                        propertyLocalDao.createThrowExceptions(property);
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

        EditText text = (EditText) findViewById(R.id.notice_activity_text);
        EditText title = (EditText) findViewById(R.id.notice_activity_title);

        final NoticeWithProperties noticeWithProperties =
                (NoticeWithProperties) getIntent().getSerializableExtra(MainActivity.EXTRA_KEY_NOTICE_WITH_PROPERTIES);

        Log.d(getClass().getName(),
                "getIntent().getSerializableExtra(MainActivity.EXTRA_KEY_NOTICE_WITH_PROPERTIES) -> " + noticeWithProperties);

        if(noticeWithProperties != null){
            Map<Integer, List<Property>> propertiesMap = noticeWithProperties.getPropertiesMap();
            if(propertiesMap.containsKey(PropertyType.TEXT)){
                List<Property> list = propertiesMap.get(PropertyType.TEXT);
                if(!list.isEmpty()){
                    text.setText(list.get(0).getText());
                }
            }

            if(propertiesMap.containsKey(PropertyType.TITLE)){
                List<Property> list = propertiesMap.get(PropertyType.TITLE);
                if(!list.isEmpty()){
                    title.setVisibility(View.VISIBLE);
                    title.setText(list.get(0).getText());
                }
            }
        }

        Button save = (Button) findViewById(R.id.notice_activity_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noticeWithProperties == null){
                    if(createNoticeTask == null){
                        createNoticeTask = new CreateNoticeTask();
                        createNoticeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    if(updateNoticeTask == null){
                        updateNoticeTask = new UpdateNoticeTask();
                        updateNoticeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, noticeWithProperties);
                    }
                }
            }
        });
    }


    private Map<Integer, List<Property>> getNewMapOfProperties(String noticeId){
        Map<Integer, List<Property>> map = new HashMap<Integer, List<Property>>();
        List<Integer> propertyTypes = Arrays.asList(
                PropertyType.TITLE, PropertyType.CREATE_DATE, PropertyType.TEXT);
        for(Integer type : propertyTypes){
            map.put(type, getProperties(type, noticeId));
        }
        return map;
    }

    private List<Property> getProperties(Integer propertyType, String noticeId){
        List<Property> properties = new ArrayList<Property>();
        switch(propertyType) {
            case PropertyType.TITLE: {
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
            case PropertyType.TEXT: {
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
            case PropertyType.CREATE_DATE: {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem itemAddTitle = menu.add(R.string.action_add_remove_title);
        itemAddTitle.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                View view = findViewById(R.id.notice_activity_title);
                if(view.getVisibility() == View.GONE) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                return true;
            }
        });

        return true;
    }

}

