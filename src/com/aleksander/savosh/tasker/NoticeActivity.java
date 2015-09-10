package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.aleksander.savosh.tasker.crypt.CryptException;
import com.aleksander.savosh.tasker.model.object.Config;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.model.object.PropertyType;
import com.aleksander.savosh.tasker.service.NoticeService;
import com.aleksander.savosh.tasker.service.PropertyService;

import java.util.*;

public class NoticeActivity extends Activity {

    public static final String EXTRA_NOTICE_ID = "com.aleksander.savosh.tasker.EXTRA_NOTICE_ID";

    public static DeleteNoticeTask deleteNoticeTask;
    public static class DeleteNoticeTask extends AsyncTask<String, Void, Boolean> {
        private Activity currActivity;
        public void setCurrActivity(Activity currActivity) {
            this.currActivity = currActivity;
        }
        @Override
        protected Boolean doInBackground(String... params) {
            String noticeId = params[0];
            try {
                NoticeService.deleteNotice(noticeId);
                return true;
            } catch(Exception e){
                Log.e(getClass().getName(), e.getMessage());
                Log.d(getClass().getName(), e.getMessage(), e);
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                Intent intent = new Intent(Application.getContext(), MainActivity.class);
                currActivity.startActivity(intent);
                currActivity.finish();
            }
            deleteNoticeTask = null;
        }
    }

    public UpdateNoticeTask updateNoticeTask;
    public class UpdateNoticeTask extends AsyncTask<Notice, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Notice... params) {
            try {
                Notice notice = params[0];

                Map<Integer, List<Property>> existsPropsMap = PropertyService.convertToMap(notice.getProperties());
                Map<Integer, List<Property>> newPropsMap = getNewMapOfProperties();

                List<Property> forSave = new ArrayList<Property>();
                for(Integer key : newPropsMap.keySet()){
                    if(existsPropsMap.containsKey(key)){//если тип таких свойтсв существовали, смотрим дальше

                        List<Property> existsProps = existsPropsMap.get(key);
                        List<Property> newProps = newPropsMap.get(key);

                        if(key == PropertyType.CREATE_DATE){
                            forSave.addAll(existsProps);
                            continue;
                        }

                        if(existsProps.size() == newProps.size()){// если количество свойст по типу совпадает, обновляем
                            for(int i = 0; i < existsProps.size(); i++){
                                Property existsProp = existsProps.get(i);
                                Property newProp = newProps.get(i);
                                forSave.add(new Property(
                                        existsProp.getObjectId(),
                                        existsProp.getCreatedAt(),
                                        existsProp.getUpdatedAt(),
                                        newProp.getType(),
                                        newProp.getText(),
                                        newProp.getDate()
                                        ));
                            }
                        } else {
                            forSave.addAll(newProps);
                        }

                    } else { //если раньше такого свойства не встречалось, смело сохраняем
                        forSave.addAll(newPropsMap.get(key));
                    }
                }

                notice.setProperties(forSave);

                NoticeService.updateNotice(notice);

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
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(!aBoolean) {
                Toast.makeText(Application.getContext(), "Cannt create notice", Toast.LENGTH_LONG).show();
            }

            if(aBoolean){
                Intent intent = new Intent(Application.getContext(), MainActivity.class);
                NoticeActivity.this.startActivity(intent);
                NoticeActivity.this.finish();
            }
            createNoticeTask = null;
        }
    }

    public final static String BROADCAST_ACTION = "com.aleksander.savosh.tasker.BROADCAST_ACTION";
    public final static String PARAM_PASSWORD = "PASSWORD";
    private BroadcastReceiver passwordReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_activity);

        final EditText text = (EditText) findViewById(R.id.notice_activity_text);
        EditText title = (EditText) findViewById(R.id.notice_activity_title);

        String noticeId = getIntent().getStringExtra(EXTRA_NOTICE_ID);

        Log.d(getClass().getName(), "GET EXTRA_NOTICE_ID: " + noticeId);

        Application application = (Application) getApplicationContext();
        Config config = application.getConfig();
        Notice notice = null;

        if(!StringUtil.isEmpty(noticeId)) {
            String accountId = StringUtil.isEmpty(config.accountId) ? Config.ACC_ZERO : config.accountId;
            for(Notice note : application.getAccounts().get(accountId).getNotices()){
                if(note.getObjectId().equals(noticeId)) {
                    notice = note;
                    break;
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

        passwordReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String receivedPassword = intent.getStringExtra(PARAM_PASSWORD);
                if(!StringUtil.isEmpty(receivedPassword)){
                    String textForCode = text.getText().toString();
                    try {
                        Log.d(getClass().getName(), "TEXT: " + textForCode);
                        if (NoticeActivity.this.mode == Mode.ENCODE) {
                            textForCode = Application.getCrypt().encrypt(receivedPassword, textForCode);
                        } else if (NoticeActivity.this.mode == Mode.DECODE) {
                            textForCode = Application.getCrypt().decrypt(receivedPassword, textForCode);
                        }
                        Log.d(getClass().getName(), "TEXT: " + textForCode);
                    } catch(CryptException e){
                        Log.e(getClass().getName(), e.getMessage());
                        Log.d(getClass().getName(), e.getMessage(), e);
                        Toast.makeText(Application.getContext(), getResources().getString(R.string.crypt_error),
                                Toast.LENGTH_LONG).show();
                    }
                    text.setText(textForCode);
                }
            }
        };
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(passwordReceiver, intFilt);
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

    enum Mode {
        NONE, ENCODE, DECODE;
    }

    private Mode mode = Mode.NONE;

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

        MenuItem itemEncode = menu.add(R.string.action_encode_notice);
        itemEncode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mode = Mode.ENCODE;
                DialogFragment dialog = new GetPasswordDialog();
                dialog.show(getFragmentManager(), "DIALOG");
                return true;
            }
        });

        MenuItem itemDecode = menu.add(R.string.action_decode_notice);
        itemDecode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mode = Mode.DECODE;
                DialogFragment dialog = new GetPasswordDialog();
                dialog.show(getFragmentManager(), "DIALOG");
                return true;
            }
        });

        final String noticeId = getIntent().getStringExtra(EXTRA_NOTICE_ID);
        if(deleteNoticeTask != null){
            deleteNoticeTask.setCurrActivity(this);
        }
        if(!StringUtil.isEmpty(noticeId)) {
            MenuItem itemRemove = menu.add(R.string.action_remove_notice);
            itemRemove.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(deleteNoticeTask == null){
                        deleteNoticeTask = new DeleteNoticeTask();
                        deleteNoticeTask.setCurrActivity(NoticeActivity.this);
                        deleteNoticeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, noticeId);
                    } else {
                        Toast.makeText(Application.getContext(), "Wait for deletion notice operation", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
        }

        return true;
    }

}

