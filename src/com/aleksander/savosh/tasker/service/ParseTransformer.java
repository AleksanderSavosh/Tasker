package com.aleksander.savosh.tasker.service;

import android.util.Log;
import com.parse.ParseObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ParseTransformer<Obj> {

    private Class<Obj> objClass;

    public ParseTransformer(Class<Obj> objClass) {
        this.objClass = objClass;
    }

    public Obj fromParseObject(ParseObject parseObject) throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {
        Log.d(getClass().getName(), "--- Transform from parse object --- ");
        Constructor<Obj> constructor = objClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Obj obj = constructor.newInstance();
        for(Field field : objClass.getDeclaredFields()){
            Log.d(getClass().getName(), "Field type: " + field.getType());
            if(field.getType() == String.class){
                field.setAccessible(true);
                field.set(obj, parseObject.getString(field.getName()));
                Log.d(getClass().getName(), "Field set: " +
                        " name: " + field.getName() +
                        " value: " + parseObject.getString(field.getName()) +
                        " result:" + obj.toString());
            }
        }
        for(Field field : objClass.getSuperclass().getDeclaredFields()) {
            Log.d(getClass().getName(), "Superclass field type: " + field.getType());
            if(field.getType() == String.class){
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = parseObject.get(fieldName);

                if(value == null){
                    if(fieldName.equals("objectId")){
                        value = parseObject.getObjectId();
                    }
                }

                field.set(obj, value);
                Log.d(getClass().getName(), "Superclass field set: " +
                        " name: " + field.getName() +
                        " value: " + value +
                        " result:" + obj.toString());
            }
        }
        Log.d(getClass().getName(), "-------------------------------");
        return obj;
    }


}
