package com.aleksander.savosh.tasker.service;

import android.util.Log;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ParseCloudDaoImpl<Obj> implements CloudDao<Obj> {

    private final Class<Obj> objClass;

    public ParseCloudDaoImpl(Class<Obj> objClass) {
        this.objClass = objClass;
    }

    @Override
    public Obj create(Obj obj) {
        try {
            return createThrowExceptions(obj);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public Obj createThrowExceptions(Obj obj) throws IllegalAccessException, ParseException, InstantiationException, DataNotFoundException {
        try {
            ParseObject parseObject = new ParseObject(objClass.getSimpleName());
            for (Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);
                parseObject.put(field.getName(), field.get(obj));
            }
            parseObject.save();
            return fromParseObject(parseObject);
        } catch (ParseException e){
            if(e.getCode() == 101){
                throw new DataNotFoundException();
            }
            throw e;
        }
    }


    @Override
    public List<Obj> read(Obj constraintObj) {
        try {
            return readThrowExceptions(constraintObj);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return new ArrayList<Obj>();
    }

    @Override
    public List<Obj> readThrowExceptions(Obj constraintObj) throws IllegalAccessException, ParseException, InstantiationException, DataNotFoundException {
        Log.d(getClass().getName(), objClass.getSimpleName());
        try {
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(objClass.getSimpleName());
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
        } catch (ParseException e){
            if(e.getCode() == 101){
                throw new DataNotFoundException();
            }
            throw e;
        }
    }


    @Override
    public Obj readFirst(Obj constraintObj) {
        List<Obj> objList = read(constraintObj);
        if(objList.size() > 0){
            return objList.get(0);
        }
        return null;
    }

    @Override
    public Obj readFirstThrowExceptions(Obj constraintObj) throws ParseException, IllegalAccessException, InstantiationException, DataNotFoundException {
        List<Obj> objList = readThrowExceptions(constraintObj);
        if(objList.size() > 0){
            return objList.get(0);
        }
        throw new DataNotFoundException();
    }


    private Obj fromParseObject(ParseObject parseObject) throws IllegalAccessException, InstantiationException {
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
    }
}
