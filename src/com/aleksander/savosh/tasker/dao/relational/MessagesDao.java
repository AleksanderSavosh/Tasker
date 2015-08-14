package com.aleksander.savosh.tasker.dao.relational;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.aleksander.savosh.tasker.beans.Message;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MessagesDao {

    public static List<Message> select(Context context){
        List<Message> messages = new ArrayList<Message>();

        DataBaseHelper sqlHelper = new DataBaseHelper(context);
        SQLiteDatabase sdb = sqlHelper.getWritableDatabase();
        String query = "select * from messages";

        Cursor cursor = sdb.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Message message = new Message();
            message.setId(cursor.getInt(cursor.getColumnIndex(sqlHelper._ID)));

            int typeId = cursor.getInt(cursor.getColumnIndex("t_id"));
            message.setType(TypesDao.selectById(context, typeId));

//            message.setDateStr(cursor.getString(cursor.getColumnIndex("m_date")));
            message.setPriority(cursor.getInt(cursor.getColumnIndex("m_priority")));
            message.setMessage(cursor.getString(cursor.getColumnIndex("m_message")));

            messages.add(message);
        }
        cursor.close();
        sdb.close();
        sqlHelper.close();

        return new ArrayList<Message>();
    }

    public static void insert(Context context, Message message){
        List<Message> messages = new ArrayList<Message>();
        messages.add(message);
        insert(context, messages);
    }

    public static void insert(Context context, List<Message> messages){
        DataBaseHelper sqlHelper = new DataBaseHelper(context);
        SQLiteDatabase sdb = sqlHelper.getWritableDatabase();

        for(Message message : messages) {
            ContentValues cv = new ContentValues();
            cv.put("t_id", message.getType().getId());
//            cv.put("m_date", message.getDateStr());
            cv.put("m_priority", message.getPriority());
            cv.put("m_message", message.getMessage());
            sdb.insert("messages", null, cv);
        }

        sdb.close();
        sqlHelper.close();
    }




}
