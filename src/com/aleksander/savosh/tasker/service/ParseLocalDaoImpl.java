package com.aleksander.savosh.tasker.service;

import android.util.Log;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ParseLocalDaoImpl<Obj> implements LocalDao<Obj> {

    private final Class<Obj> objClass;

    public ParseLocalDaoImpl(Class<Obj> objClass) {
        this.objClass = objClass;
    }

    @Override
    public Obj create(Obj obj) {
        try {
            ParseObject parseObject = new ParseObject(objClass.getSimpleName());
            for (Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);
                parseObject.put(field.getName(), field.get(obj));
            }
            parseObject.pin();
            return fromParseObject(parseObject);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public List<Obj> read(Obj constraintObj) {
        try {
            Log.d(getClass().getName(), objClass.getSimpleName());
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(objClass.getSimpleName());
            parseQuery.fromLocalDatastore();
            for(Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(constraintObj);
                if(value != null) {
                    parseQuery.whereEqualTo(field.getName(), value);
                }
            }
            List<ParseObject> parseObjects = parseQuery.find();
            List<Obj> objList = new ArrayList<Obj>();
            for(ParseObject parseObject : parseObjects){
                objList.add(fromParseObject(parseObject));
            }
            return objList;
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return new ArrayList<Obj>();
    }

    @Override
    public Obj readFirst(Obj constraintObj) {
        List<Obj> objList = read(constraintObj);
        if(objList.size() > 0){
            return objList.get(0);
        }
        return null;
    }

    private Obj fromParseObject(ParseObject parseObject){
        try {
            Obj obj = objClass.newInstance();
            for(Field field : objClass.getDeclaredFields()){
                Log.d(getClass().getName(), "Field type: " + field.getType());
                if(field.getType() == String.class){
                    field.setAccessible(true);
                    field.set(obj, parseObject.getString(field.getName()));
                    Log.d(getClass().getName(), "Field name: " + field.getName() + " " + obj.toString());
                }
            }
            for(Field field : objClass.getSuperclass().getDeclaredFields()) {
                if(field.getType() == String.class){
                    field.setAccessible(true);
                    field.set(obj, parseObject.getString(field.getName()));
                }
            }
            return obj;
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }
}
