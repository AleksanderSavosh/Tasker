package com.aleksander.savosh.tasker.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.aleksander.savosh.tasker.R;
import com.aleksander.savosh.tasker.beans.Message;
import com.aleksander.savosh.tasker.beans.Status;
import com.aleksander.savosh.tasker.beans.Type;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

@Deprecated
public class OrmDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "notes_orm.db";
    private static final int DATABASE_VERSION = 7;

    private Dao<Type, Integer> typesDao = null;
    private RuntimeExceptionDao<Type, Integer> typesRuntimeDao = null;

    private Dao<Status, Integer> statusesDao = null;
    private RuntimeExceptionDao<Status, Integer> statusesRuntimeDao = null;

    private Dao<Message, Integer> messagesDao = null;
    private RuntimeExceptionDao<Message, Integer> messagesRuntimeDao = null;

    private Context context;

    public OrmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(getClass().getName(), "onCreate");
            TableUtils.createTable(connectionSource, Type.class);
            TableUtils.createTable(connectionSource, Status.class);
            TableUtils.createTable(connectionSource, Message.class);
        } catch (SQLException e) {
            Log.e(getClass().getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        // here we try inserting data in the on-create as a test
        RuntimeExceptionDao<Type, Integer> typesDao = getTypesRuntimeDao();
        RuntimeExceptionDao<Status, Integer> statusesDao = getStatusesRuntimeDao();
        RuntimeExceptionDao<Message, Integer> messagesDao = getMessagesRuntimeDao();

        String[] typesArr = context.getResources().getStringArray(R.array.default_types);

        // create some entries in the onCreate
        Type type = new Type(typesArr[0], R.drawable.tea24);
        typesDao.create(type);
        type = new Type(typesArr[1], R.drawable.cart10);
        typesDao.create(type);
        type = new Type(typesArr[2], R.drawable.cube4);
        typesDao.create(type);
        type = new Type(typesArr[3], R.drawable.businessman252);
        typesDao.create(type);

        String[] statusesArr = context.getResources().getStringArray(R.array.default_statuses);
        for(String status : statusesArr){
            statusesDao.create(new Status(status));
        }

        Type typeBuy = typesDao.queryForEq("name", typesArr[1]).get(0);
        Status statusNew = statusesDao.queryForEq("name", statusesArr[0]).get(0);
        Message message = new Message(typeBuy, 5, "Example message", statusNew);
        messagesDao.create(message);

        Log.i(getClass().getName(), typeBuy.toString());
        Log.i(getClass().getName(), statusNew.toString());
        Log.i(getClass().getName(), message.toString());

        Log.i(getClass().getName(), "created new entries in onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(getClass().getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Type.class, true);
            TableUtils.dropTable(connectionSource, Status.class, true);
            TableUtils.dropTable(connectionSource, Message.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(getClass().getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<Type, Integer> getTypesDao() throws SQLException {
        if(typesDao == null){
            typesDao = getDao(Type.class);
        }
        return typesDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Type, Integer> getTypesRuntimeDao() {
        if(typesRuntimeDao == null){
            typesRuntimeDao = getRuntimeExceptionDao(Type.class);
        }
        return typesRuntimeDao;
    }

    public Dao<Status, Integer> getStatusesDao() throws SQLException {
        if(statusesDao == null){
            statusesDao = getDao(Status.class);
        }
        return statusesDao;
    }

    public RuntimeExceptionDao<Status, Integer> getStatusesRuntimeDao() {
        if(statusesRuntimeDao == null){
            statusesRuntimeDao = getRuntimeExceptionDao(Status.class);
        }
        return statusesRuntimeDao;
    }

    public Dao<Message, Integer> getMessagesDao() throws SQLException {
        if(messagesDao == null){
            messagesDao = getDao(Message.class);
        }
        return messagesDao;
    }

    public RuntimeExceptionDao<Message, Integer> getMessagesRuntimeDao() {
        if(messagesRuntimeDao == null){
            messagesRuntimeDao = getRuntimeExceptionDao(Message.class);
        }
        return messagesRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        typesDao = null;
        typesRuntimeDao = null;
    }
}
