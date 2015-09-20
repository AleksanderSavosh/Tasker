package com.aleksander.savosh.tasker;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.aleksander.savosh.tasker.crypt.CryptException;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.model.object.PropertyType;
import com.aleksander.savosh.tasker.service.PropertyService;
import com.aleksander.savosh.tasker.task.CreateNoticeTask;
import com.aleksander.savosh.tasker.task.DeleteNoticeTask;
import com.aleksander.savosh.tasker.task.UpdateNoticeTask;
import com.aleksander.savosh.tasker.task.holder.NoticeTaskHolder;
import com.aleksander.savosh.tasker.util.StringUtil;

import java.util.*;

//TODO переименовать класс
public class NoticeActivity2 extends AppCompatActivity {

    public static final String EXTRA_NOTICE_ID = "com.aleksander.savosh.tasker.EXTRA_NOTICE_ID";

    public static class PropertyView {
        public Property property;
        public View view;

        public PropertyView(Property property, View view) {
            this.property = property;
            this.view = view;
        }
    }

    private Notice notice;
    private Map<Integer,List<PropertyView>> propViewMap;

    private EditText titleEditText;
    private EditText textEditText;
    private Button saveButton;
    private View saveProgressBar;
    NoticeTaskHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "--- === ON CREATE NOTICE ACTIVITY === ---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_activity);

        titleEditText = (EditText) findViewById(R.id.notice_activity_title);
        textEditText = (EditText) findViewById(R.id.notice_activity_text);
        saveButton = (Button) findViewById(R.id.notice_activity_save);
        saveProgressBar = findViewById(R.id.notice_activity_progress_bar);

        holder = new NoticeTaskHolder(){{
            this.activity = NoticeActivity2.this;
            this.titleEditText = NoticeActivity2.this.titleEditText;
            this.textEditText = NoticeActivity2.this.textEditText;
            this.button = NoticeActivity2.this.saveButton;
            this.progressBar = NoticeActivity2.this.saveProgressBar;
        }};

        String noticeId = getIntent().getStringExtra(EXTRA_NOTICE_ID);
        Log.d(getClass().getName(), "GET EXTRA_NOTICE_ID: " + noticeId);

        if(!StringUtil.isEmpty(noticeId)) {
            notice = Application.getNoticeLocalDao().readWithRelations(noticeId);
            if(notice != null){
                PropertyService.validateProperties(notice.getProperties());
                propViewMap = createPropertyViewMap(notice.getProperties());
            }
        }

        Application.getAsyncTaskManager().updateTask(CreateNoticeTask.class, holder);
        Application.getAsyncTaskManager().updateTask(UpdateNoticeTask.class, holder);
        Application.getAsyncTaskManager().updateTask(DeleteNoticeTask.class, holder);
        findViewById(R.id.notice_activity_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notice != null){ // edit mode
                    notice.setProperties(getPropertiesForUpdate());
                    Application.getAsyncTaskManager().startTask(UpdateNoticeTask.class, holder, notice);
                } else { // create mode
                    Application.getAsyncTaskManager().startTask(CreateNoticeTask.class, holder, getPropertiesForCreate());
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.notice_activity_title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO блокировать менюшки когда выполняется AsyncTask?

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notice, menu);

        MenuItem itemAddTitle = menu.findItem(R.id.menu_notice_title);
        MenuItem itemEncode = menu.findItem(R.id.menu_notice_encode);
        MenuItem itemDecode = menu.findItem(R.id.menu_notice_decode);
        MenuItem itemDelete = menu.findItem(R.id.menu_notice_delete);

        itemAddTitle.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(titleEditText.getVisibility() == View.GONE) {
                    titleEditText.setVisibility(View.VISIBLE);
                } else {
                    titleEditText.setVisibility(View.GONE);
                }
                return true;
            }
        });

        itemEncode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showEncodeDecodeDialog(ENCODE_MODE);
                return true;
            }
        });

        itemDecode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showEncodeDecodeDialog(DECODE_MODE);
                return true;
            }
        });

        Application.getAsyncTaskManager().updateTask(DeleteNoticeTask.class, holder);
        if(notice != null) {
            itemDelete.setVisible(true);
            itemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Application.getAsyncTaskManager().startTask(DeleteNoticeTask.class, holder, notice);
                    return true;
                }
            });
        }

        return true;
    }

    public static final int ENCODE_MODE = 99999;
    public static final int DECODE_MODE = 99998;
    private void showEncodeDecodeDialog(final int mode){
        final AlertDialog.Builder builder = new AlertDialog.Builder(NoticeActivity2.this, R.style.CustomAlertDialogStyle);
        if(mode == ENCODE_MODE) {
            builder.setTitle(R.string.dialog_title_encode_notice);
        } else if(mode == DECODE_MODE){
            builder.setTitle(R.string.dialog_title_decode_notice);
        }
        View root = getLayoutInflater().inflate(R.layout.encode_decode_notice_dialog, null);
        final AppCompatEditText input = (AppCompatEditText) root.findViewById(R.id.encode_decode_dialog_edit_text);
        builder.setView(root);
        builder.setMessage(R.string.dialog_message_enter_password);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = textEditText.getText().toString();
                String key = input.getText().toString();
                if(!StringUtil.isEmpty(key)) {
                    try {
                        if(mode == ENCODE_MODE) {
                            text = Application.getCrypt().encrypt(text, key);
                        } else if(mode == DECODE_MODE) {
                            text = Application.getCrypt().decrypt(text, key);
                        }
                    } catch (CryptException e){
                        Log.e(getClass().getName(), e.getMessage());
                        Log.d(getClass().getName(), e.getMessage(), e);
                        Toast.makeText(Application.getContext(), R.string.crypt_error, Toast.LENGTH_LONG).show();
                    }
                }
                textEditText.setText(text);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private Map<Integer, List<PropertyView>> createPropertyViewMap(List<Property> properties){
        Map<Integer,List<PropertyView>> propMap = new HashMap<Integer, List<PropertyView>>();
        for(Property property : properties){
            PropertyView propertyView;
            switch (property.getType()){
                case PropertyType.TEXT:
                    textEditText.setText(property.getText());
                    textEditText.setVisibility(View.VISIBLE);
                    propertyView = new PropertyView(property, textEditText);
                    break;
                case PropertyType.TITLE:
                    titleEditText.setText(property.getText());
                    titleEditText.setVisibility(View.VISIBLE);
                    propertyView = new PropertyView(property, titleEditText);
                    break;
                default:
                    propertyView = new PropertyView(property, null);
            }
            if(propMap.containsKey(property.getType())){
                propMap.get(property.getType()).add(propertyView);
            } else {
                propMap.put(
                        property.getType(),
                        new ArrayList<PropertyView>(Arrays.asList(propertyView))
                );
            }
        }
        return propMap;
    }

    private List<Property> getPropertiesForUpdate(){
        List<Property> propertiesForUpdate = new ArrayList<Property>();
        for(Integer propType : propViewMap.keySet()){
            for(PropertyView propertyView : propViewMap.get(propType)){
                switch (propType){
                    case PropertyType.TITLE:
                    case PropertyType.TEXT:
                        propertyView.property.setText(
                                ((EditText) propertyView.view).getText().toString()
                        );
                        break;
                }
                propertiesForUpdate.add(propertyView.property);
            }
        }
        return propertiesForUpdate;
    }

    private List<Property> getPropertiesForCreate(){
        List<Property> propertiesForCreate = new ArrayList<Property>();
        propertiesForCreate.add(new Property(PropertyType.CREATE_DATE, null, new Date()));
        if(titleEditText.getVisibility() == View.VISIBLE) {
            propertiesForCreate.add(new Property(PropertyType.TITLE, titleEditText.getText().toString(), null));
        }
        if(textEditText.getVisibility() == View.VISIBLE) {
            propertiesForCreate.add(new Property(PropertyType.TEXT, textEditText.getText().toString(), null));
        }
        return propertiesForCreate;
    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getName(), "--- === ON DESTROY NOTICE ACTIVITY === ---");
        super.onDestroy();
    }
}
