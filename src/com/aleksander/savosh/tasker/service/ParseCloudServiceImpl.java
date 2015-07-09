package com.aleksander.savosh.tasker.service;

import android.util.Log;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Field;


public class ParseCloudServiceImpl<Obj> implements CloudService<Obj> {

    private final Class<Obj> objClass;

    public ParseCloudServiceImpl(Class<Obj> objClass) {
        this.objClass = objClass;
    }

    @Override
    public Obj create(Obj obj) {
        try {
            ParseObject parseObject = new ParseObject(objClass.getSimpleName());
            for (Field field : objClass.getFields()) {
                field.setAccessible(true);
                parseObject.put(field.getName(), field.get(obj));
            }
            parseObject.save();
            return fromParseObject(parseObject);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    @Override
    public Obj read(String uniqueValue) {
        try {
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(objClass.getSimpleName());
            parseQuery.whereEqualTo(getColumnNameForUniqueValue(), uniqueValue);
            ParseObject parseObject = parseQuery.getFirst();
            return fromParseObject(parseObject);
        } catch (ParseException e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
        return null;
    }

    private String getColumnNameForUniqueValue(){
        if(objClass.getSimpleName().equals("Account")){
            return "objectId";
        } else if (objClass.getSimpleName().equals("Phone")){
            return "number";
        }
        throw new RuntimeException("Can not get pk column name");
    }

    private Obj fromParseObject(ParseObject parseObject){
        try {
            Obj obj = objClass.newInstance();
            for(Field field : objClass.getFields()){
                if(field.getType() == String.class){
                    field.setAccessible(true);
                    field.set(obj, parseObject.getString(field.getName()));
                }
            }
            for(Field field : objClass.getSuperclass().getFields()) {
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
