package com.aleksander.savosh.tasker.dao.relational.parse;

import android.util.Log;
import com.aleksander.savosh.tasker.dao.ReflectionUtil;
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

        for(Field field : ReflectionUtil.getAllFields(objClass)){
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = parseObject.get(fieldName);
            if(value == null){
                if(fieldName.equals("objectId")){
                    value = parseObject.getObjectId();
                }
                if(fieldName.equals("createdAt")){
                    value = parseObject.getCreatedAt();
                }
                if(fieldName.equals("updatedAt")){
                    value = parseObject.getUpdatedAt();
                }
            }
            Log.d(getClass().getName(), "field set: name: " + fieldName + " value: " + value);
            field.set(obj, value);
        }
        Log.d(getClass().getName(), "-------------------------------");
        return obj;
    }


}
