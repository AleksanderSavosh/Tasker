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
import android.widget.Toast;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.model.object.PropertyType;
import com.aleksander.savosh.tasker.service.NoticeService;
import com.aleksander.savosh.tasker.service.PropertyService;

import java.util.*;

public class NoticeActivity extends Activity {

    public static final String EXTRA_NOTICE_ID = "com.aleksander.savosh.tasker.EXTRA_NOTICE_ID";

    public UpdateNoticeTask updateNoticeTask;
    public class UpdateNoticeTask extends AsyncTask<Notice, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Notice... params) {
            try {
//                Notice oldNoticeWithProperties = params[0];
//
//                Map<Integer, List<Property>> newPropertiesMap = NoticeActivity.this
//                        .getNewMapOfProperties(oldNoticeWithProperties.getNotice().getObjectId());
//
//                Set<Integer> newNoticeTypes = new HashSet<Integer>();
//                Set<Integer> commonNoticeTypes = new HashSet<Integer>();
//                Set<Integer> oldNoticeTypes = new HashSet<Integer>();
//
//                newNoticeTypes.addAll(newPropertiesMap.keySet());
//                oldNoticeTypes.addAll(oldNoticeWithProperties.getPropertiesMap().keySet());
//
//                newNoticeTypes.remove(PropertyType.CREATE_DATE);
//                oldNoticeTypes.remove(PropertyType.CREATE_DATE);
//
//                Set<Integer> allNoticeTypes = new HashSet<Integer>();
//                allNoticeTypes.addAll(newNoticeTypes);
//                allNoticeTypes.addAll(oldNoticeTypes);
//
//                for(Integer key : allNoticeTypes){
//                    if(newNoticeTypes.contains(key) && oldNoticeTypes.contains(key)){
//                        commonNoticeTypes.add(key);
//                        newNoticeTypes.remove(key);
//                        oldNoticeTypes.remove(key);
//                    }
//                }
//
//                LocalDao<Property> propertyLocalDao = Application.getPropertyLocalDao();
//
//                for(Integer key : newNoticeTypes){
//                    for(Property property : newPropertiesMap.get(key)){
//                        propertyLocalDao.createThrowExceptions(property);
//                    }
//                }
//
//                for(Integer key : oldNoticeTypes){
//                    for(Property property : oldNoticeWithProperties.getPropertiesMap().get(key)){
//                        propertyLocalDao.deleteThrowExceptions(property);
//                    }
//                }
//
//                for(Integer key : commonNoticeTypes){
//                    List<Property> newProperties = newPropertiesMap.get(key);
//                    List<Property> oldProperties = oldNoticeWithProperties.getPropertiesMap().get(key);
//
//                    for(Property property : oldProperties){
//                        propertyLocalDao.deleteThrowExceptions(property);
//                    }
//
//                    for(Property property : newProperties){
//                        propertyLocalDao.createThrowExceptions(property);
//                    }
//                }

            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
                Log.d(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString(), e);
                Toast.makeText(Application.getContext(), "Cannt update notice", Toast.LENGTH_LONG).show();
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

                Map<Integer, List<Property>> propertiesMap = NoticeActivity.this.getNewMapOfProperties();
                NoticeService.createNotice(propertiesMap);

            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
                Log.d(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString(), e);
                Toast.makeText(Application.getContext(), "Cannt create notice", Toast.LENGTH_LONG).show();
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

        String noticeId = getIntent().getStringExtra(EXTRA_NOTICE_ID);

        Log.d(getClass().getName(), "GET EXTRA_NOTICE_ID: " + noticeId);

        Application application = (Application) getApplicationContext();
        Config config = application.getConfig();
        Notice notice = null;

        if(!StringUtil.isEmpty(noticeId)) {
            if (StringUtil.isEmpty(config.rememberMeAccountId)) {
                notice = application.getLocalNotices().get(noticeId);
            } else {
                for(Notice note : application.getAccounts().get(config.rememberMeAccountId).getNotices()){
                    if(note.getObjectId().equals(noticeId)) {
                        notice = note;
                        break;
                    }
                }
            }
        }

        if(notice != null){
            Map<Integer, List<Property>> propertiesMap = PropertyService.convertToMap(notice.getProperties());
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
        final Notice noticeForClickListener = notice;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noticeForClickListener == null){
                    if(createNoticeTask == null){
                        createNoticeTask = new CreateNoticeTask();
                        createNoticeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    if(updateNoticeTask == null){
                        updateNoticeTask = new UpdateNoticeTask();
                        updateNoticeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, noticeForClickListener);
                    }
                }
            }
        });
    }

    private Map<Integer, List<Property>> getNewMapOfProperties(){
        Map<Integer, List<Property>> map = new HashMap<Integer, List<Property>>();
        List<Integer> propertyTypes = Arrays.asList(
                PropertyType.TITLE, PropertyType.CREATE_DATE, PropertyType.TEXT);
        for(Integer type : propertyTypes){
            map.put(type, getProperties(type));
        }
        return map;
    }

    private List<Property> getProperties(Integer propertyType){
        List<Property> properties = new ArrayList<Property>();
        switch(propertyType) {
            case PropertyType.TITLE: {
                EditText editText = (EditText) findViewById(R.id.notice_activity_title);
                if(editText.getVisibility() == View.VISIBLE){
                    Property property = new Property(propertyType, editText.getText().toString(), null);
                    properties.add(property);
                }
                break;
            }
            case PropertyType.TEXT: {
                EditText editText = (EditText) findViewById(R.id.notice_activity_text);
                if(editText.getVisibility() == View.VISIBLE){
                    Property property = new Property(propertyType, editText.getText().toString(), null);
                    properties.add(property);
                }
                break;
            }
            case PropertyType.CREATE_DATE: {
                Property property = new Property(propertyType, null, new Date());
                properties.add(property);
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

