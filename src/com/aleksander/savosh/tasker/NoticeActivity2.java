package com.aleksander.savosh.tasker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.aleksander.savosh.tasker.crypt.CryptException;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.model.object.PropertyType;
import com.aleksander.savosh.tasker.service.PropertyService;
import com.aleksander.savosh.tasker.task.CreateNoticeTask;
import com.aleksander.savosh.tasker.task.DeleteNoticeTask;
import com.aleksander.savosh.tasker.task.UpdateNoticeTask;
import com.aleksander.savosh.tasker.util.StringUtil;

import java.util.*;

public class NoticeActivity2 extends Activity {

    public static final String EXTRA_NOTICE_ID = "com.aleksander.savosh.tasker.EXTRA_NOTICE_ID";

    public static class PropertyView {
        public Property property;
        public View view;

        public PropertyView(Property property, View view) {
            this.property = property;
            this.view = view;
        }
    }

    public static class EncodeDecodeDialog extends DialogFragment {
        public static final Integer ENCODE = 1;
        public static final Integer DECODE = 2;
        public static EncodeDecodeDialog newInstance(int mode){
            EncodeDecodeDialog dialog = new EncodeDecodeDialog();
            Bundle args = new Bundle();
            args.putInt("mode", mode);
            dialog.setArguments(args);
            return dialog;
        }
        public EncodeDecodeDialog(){}
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final int mode = getArguments().getInt("mode");
            final EditText passwordEditText = new EditText(getActivity());
            final EditText textEditText = ((NoticeActivity2) getActivity()).textEditText;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String text = textEditText.getText().toString();
                            String password = passwordEditText.getText().toString();
                            if(!StringUtil.isEmpty(password)) {
                                try {
                                    if (mode == ENCODE) {
                                        text = Application.getCrypt().encrypt(text, password);
                                    } else if (mode == DECODE) {
                                        text = Application.getCrypt().decrypt(text, password);
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
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    })
                    .setView(passwordEditText);
            return builder.create();
        }
    }

    private Notice notice;
    private Map<Integer,List<PropertyView>> propViewMap;

    private EditText titleEditText;
    private EditText textEditText;
    private Button saveButton;
    private ProgressBar saveProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_activity);

        titleEditText = (EditText) findViewById(R.id.notice_activity_title);
        textEditText = (EditText) findViewById(R.id.notice_activity_text);
        saveButton = (Button) findViewById(R.id.notice_activity_save);
        saveProgressBar = (ProgressBar) findViewById(R.id.notice_activity_progress_bar);


        String noticeId = getIntent().getStringExtra(EXTRA_NOTICE_ID);
        Log.d(getClass().getName(), "GET EXTRA_NOTICE_ID: " + noticeId);

        if(!StringUtil.isEmpty(noticeId)) {
            notice = Application.getNoticeLocalDao().readWithRelations(noticeId);
            if(notice != null){
                PropertyService.validateProperties(notice.getProperties());
                propViewMap = createPropertyViewMap(notice.getProperties());
            }
        }

        CreateNoticeTask.initTask(null, NoticeActivity2.this, saveButton, saveProgressBar, false);
        UpdateNoticeTask.initTask(null, NoticeActivity2.this, saveButton, saveProgressBar, false);
        DeleteNoticeTask.initTask(null, NoticeActivity2.this, false);
        findViewById(R.id.notice_activity_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notice != null){ // edit mode
                    notice.setProperties(getPropertiesForUpdate());
                    UpdateNoticeTask.initTask(notice ,NoticeActivity2.this, saveButton, saveProgressBar, true);
                } else { // create mode
                    CreateNoticeTask.initTask(getPropertiesForCreate(), NoticeActivity2.this, saveButton, saveProgressBar, true);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem itemAddTitle = menu.add(R.string.action_add_remove_title);
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

        MenuItem itemEncode = menu.add(R.string.action_encode_notice);
        itemEncode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DialogFragment dialog = EncodeDecodeDialog.newInstance(EncodeDecodeDialog.ENCODE);
                dialog.show(getFragmentManager(), "DIALOG");
                return true;
            }
        });

        MenuItem itemDecode = menu.add(R.string.action_decode_notice);
        itemDecode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DialogFragment dialog = EncodeDecodeDialog.newInstance(EncodeDecodeDialog.DECODE);
                dialog.show(getFragmentManager(), "DIALOG");
                return true;
            }
        });

        DeleteNoticeTask.initTask(null, NoticeActivity2.this, false);
        if(notice != null) {
            MenuItem itemRemove = menu.add(R.string.action_remove_notice);
            itemRemove.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    DeleteNoticeTask.initTask(notice, NoticeActivity2.this, true);
                    return true;
                }
            });
        }

        return true;
    }

    private Map<Integer, List<PropertyView>> createPropertyViewMap(List<Property> properties){
        Map<Integer,List<PropertyView>> propMap = new HashMap<Integer, List<PropertyView>>();
        for(Property property : properties){
            PropertyView propertyView;
            switch (property.getType()){
                case PropertyType.TEXT:
                    textEditText.setText(property.getText());
                    propertyView = new PropertyView(property, textEditText);
                    break;
                case PropertyType.TITLE:
                    titleEditText.setText(property.getText());
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
}
