package com.aleksander.savosh.tasker.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

@Deprecated
public class DataBaseHelper extends SQLiteOpenHelper implements BaseColumns {

    // константы для конструктора
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 6;

    private static final String SQL_CREATE_TABLE_TYPES = "create table types (" + BaseColumns._ID + ", t_name nvarchar(255));";
    private static final String SQL_CREATE_TABLE_MESSAGES
            = "create table messages(" + BaseColumns._ID + ", t_id integer, m_date varchar(255), m_priority integer, m_message nvarchar(1024));";


    private static final String SQL_INSERT_TYPES
            = "insert into types (t_name)"
            +" select 'Meet'"
            + "union select 'Buy'"
            + "union select  'Important thing'"
            + "union select  'Housework'"
            + "union select  'Self-development'"
            + "union select  'Study'";

    private static final String SQL_DELETE_TABLE_TYPES = "drop table if exists types";
    private static final String SQL_DELETE_TABLE_MESSAGES = "drop table if exists messages";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_TYPES);
        sqLiteDatabase.execSQL(SQL_INSERT_TYPES);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w("LOG_TAG", "Обновление базы данных с версии " + oldVersion
                + " до версии " + newVersion + ", которое удалит все старые данные");
        // Удаляем предыдущую таблицу при апгрейде
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_TYPES);
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE_MESSAGES);
        // Создаём новый экземпляр таблицы
        onCreate(sqLiteDatabase);
    }
}
