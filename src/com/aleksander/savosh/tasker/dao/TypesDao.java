package com.aleksander.savosh.tasker.dao;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.aleksander.savosh.tasker.beans.Type;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TypesDao {

    public static String[] select(Context context){
        List<String> result = new ArrayList<String>();

        DataBaseHelper sqlHelper = new DataBaseHelper(context);
        SQLiteDatabase sdb = sqlHelper.getWritableDatabase();

        String query = "select * from types";
        Cursor cursor = sdb.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("t_name"));
            result.add(name);
        }
        cursor.close();

        sdb.close();
        sqlHelper.close();

        return result.toArray(new String[result.size()]);
    }

    public static List<Type> selectAsEntry(Context context){
        List<Type> types = new ArrayList<Type>();
        DataBaseHelper sqlHelper = null;
        SQLiteDatabase sdb = null;
        Cursor cursor = null;
        try {
            sqlHelper = new DataBaseHelper(context);
            sdb = sqlHelper.getWritableDatabase();

            String query = "select * from types";
            cursor = sdb.rawQuery(query, null);
            while (cursor.moveToNext()) {
                Type type = new Type();
                type.setId(cursor.getInt(cursor.getColumnIndex(sqlHelper._ID)));
                type.setName(cursor.getString(cursor.getColumnIndex("t_name")));
                types.add(type);
            }
        } finally {
            try{cursor.close();} catch(Exception unused){}
            try{sdb.close();} catch(Exception unused){}
            try{sqlHelper.close();} catch(Exception unused){}
        }
        return types;
    }

    public static Type selectById(Context context, int id){
        DataBaseHelper sqlHelper = null;
        SQLiteDatabase sdb = null;
        Cursor cursor = null;
        try {
            sqlHelper = new DataBaseHelper(context);
            sdb = sqlHelper.getWritableDatabase();

            String query = "select * from types where _ID = " + id;
            cursor = sdb.rawQuery(query, null);
            if (cursor.moveToNext()) {
                Type type = new Type();
                type.setId(cursor.getInt(cursor.getColumnIndex(sqlHelper._ID)));
                type.setName(cursor.getString(cursor.getColumnIndex("t_name")));
                return type;
            }
        } finally {
            try{cursor.close();} catch(Exception unused){}
            try{sdb.close();} catch(Exception unused){}
            try{sqlHelper.close();} catch(Exception unused){}
        }
        return null;
    }



}
