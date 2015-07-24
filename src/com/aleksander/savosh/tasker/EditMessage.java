package com.aleksander.savosh.tasker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.aleksander.savosh.tasker.adapters.TypeSpinnerAdapter;
import com.aleksander.savosh.tasker.beans.Message;
import com.aleksander.savosh.tasker.beans.Status;
import com.aleksander.savosh.tasker.beans.Type;
import com.aleksander.savosh.tasker.dao.OrmDatabaseHelper;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class EditMessage extends OrmLiteBaseActivity<OrmDatabaseHelper> {

    private static final int MODE_EDIT_MESSAGE = 158;
    private static final int MODE_CREATE_MESSAGE = 157;

    private Message buffer;
    private Status statusNew;
    private int currStatus = MODE_CREATE_MESSAGE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_message);

        statusNew = getHelper().getStatusesRuntimeDao()
                .queryForEq("name", getResources().getString(R.string.status_new)).get(0);
//
        List<Type> types = getHelper().getTypesRuntimeDao().queryForAll();
//
        Log.i(getClass().getName(), types.getClass().getName());

        Spinner spinner = (Spinner) findViewById(R.id.select_btn);
        TypeSpinnerAdapter typeSpinnerAdapter = new TypeSpinnerAdapter(this, types);
        spinner.setAdapter(typeSpinnerAdapter);


        Serializable serMessage = getIntent().getSerializableExtra("selectedMessage");
        if(serMessage != null){
            currStatus = MODE_EDIT_MESSAGE;
            buffer = (Message) serMessage;
            spinner.setSelected(true);
            spinner.setSelection(typeSpinnerAdapter.getPosition(buffer.getType()));
            ((EditText)findViewById(R.id.message)).setText(buffer.getMessage());
        } else {
            currStatus = MODE_CREATE_MESSAGE;
        }
    }


    public void click(View v){
        if(v.getId() == R.id.save_btn){

            if(currStatus == MODE_CREATE_MESSAGE){
                buffer = new Message();
                buffer.setStatus(statusNew);
                buffer.setCreateDate(new Date());
                buffer.setLastModifiedDate(buffer.getCreateDate());
                buffer.setPriority(0);
            } else if(currStatus == MODE_EDIT_MESSAGE) {
                buffer.setLastModifiedDate(new Date());
            }

            String message = ((EditText)findViewById(R.id.message)).getText().toString();
            buffer.setMessage(message);

            Spinner spinner = (Spinner) findViewById(R.id.select_btn);
            Type type = (Type) spinner.getSelectedItem();
            buffer.setType(type);

            Log.i(getClass().getName(), "buffer before: "  + buffer.toString());

            //save to storage
            if(currStatus == MODE_CREATE_MESSAGE) {
                getHelper().getMessagesRuntimeDao().create(buffer);
//                doesn't need
//                buffer = getHelper().getMessagesRuntimeDao().queryForFieldValues(
//                        ImmutableMap.<String, Object>builder()
//                                .put("message", buffer.getMessage())
//                                .put("createDate", buffer.getCreateDate())
//                                .build()
//                ).get(0);
            } else {
                getHelper().getMessagesRuntimeDao().update(buffer);
            }

            Log.i(getClass().getName(), "buffer after: "  + buffer.toString());

//            MainActivity.MESSAGES.put(buffer.getId(), buffer);
            finish();
        }
    }


}
