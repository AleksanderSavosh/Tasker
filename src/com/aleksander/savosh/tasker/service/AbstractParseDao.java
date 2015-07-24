package com.aleksander.savosh.tasker.service;

import android.util.Log;
import com.aleksander.savosh.tasker.model.Property;
import com.aleksander.savosh.tasker.model.PropertyType;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParseDao<Obj> implements BaseDao<Obj> {

    private final Class<Obj> objClass;
    private final ParseTransformer<Obj> transformer;
    private final boolean isCloudStorage;

    public AbstractParseDao(Class<Obj> objClass, boolean isCloudStorage) {
        this.objClass = objClass;
        this.transformer = new ParseTransformer<Obj>(objClass);
        this.isCloudStorage = isCloudStorage;
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
    public Obj createThrowExceptions(Obj obj) throws IllegalAccessException, ParseException, InstantiationException, DataNotFoundException, NoSuchMethodException, InvocationTargetException {
        try {
            Log.d(getClass().getName(), " === Create " + (isCloudStorage?"":"local ") + objClass.getSimpleName() +
                    " obj: " + obj + " === ");
            ParseObject parseObject = new ParseObject(objClass.getSimpleName());
            for (Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if(value != null && value.getClass().isEnum()){
                    value = value.toString();
                }
                if(value != null) {
                    Log.d(getClass().getName(), "parseObject.put(" + field.getName() + ", " + value + "); "
                            + "field type: " + field.getType());
                    parseObject.put(field.getName(), value);
                }
            }

            for (Field field : objClass.getSuperclass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if(value != null && value.getClass().isEnum()){
                    value = value.toString();
                }
                if(value != null) {
                    Log.d(getClass().getName(), "parseObject.put(" + field.getName() + ", " + value + "); "
                            + "field type: " + field.getType());
                    parseObject.put(field.getName(), value);
                }
            }

            if(isCloudStorage) {
                parseObject.save();
            } else {
                parseObject.pin();
            }
            Obj result = transformer.fromParseObject(parseObject);
            for (Field field : objClass.getDeclaredFields()) {//пишем старые значения enum и надеемся что ранее они были сохранены
                if(field.isEnumConstant()){
                    field.set(result, field.get(obj));
                }
            }
            Log.d(getClass().getName(), " Result: " + result);
            Log.d(getClass().getName(), " ============================================ ");
            return result;
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
    public List<Obj> readThrowExceptions(Obj constraintObj) throws IllegalAccessException, ParseException, InstantiationException, DataNotFoundException, NoSuchMethodException, InvocationTargetException {
        Log.d(getClass().getName(), " === Read " + (isCloudStorage?"":"local ") + objClass.getSimpleName() +
                " constraintObj: " + constraintObj + " === ");
        try {
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(objClass.getSimpleName());
            if(!isCloudStorage){
                parseQuery.fromLocalDatastore();
            }
            for(Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = constraintObj != null ? field.get(constraintObj) : null;
                if(value != null) {
                    parseQuery.whereEqualTo(field.getName(), value);
                }
            }
            List<ParseObject> parseObjects = parseQuery.find();
            List<Obj> objList = new ArrayList<Obj>();
            for(ParseObject parseObject : parseObjects){
                objList.add(transformer.fromParseObject(parseObject));
            }
            Log.d(getClass().getName(), " Result: " + objList);
            Log.d(getClass().getName(), " ============================================= ");
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
    public Obj readFirstThrowExceptions(Obj constraintObj) throws ParseException, IllegalAccessException, InstantiationException, DataNotFoundException, NoSuchMethodException, InvocationTargetException {
        List<Obj> objList = readThrowExceptions(constraintObj);
        if(objList.size() > 0){
            return objList.get(0);
        }
        throw new DataNotFoundException();
    }

    @Override
    public void delete(Obj constraintObj) {
        try {
            deleteThrowExceptions(constraintObj);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    @Override
    public void deleteThrowExceptions(Obj constraintObj) throws Exception {
        try {
            Log.d(getClass().getName(), " === Delete " + (isCloudStorage?"":"local ") + objClass.getSimpleName() +
                    " constraintObj: " + constraintObj + " === ");
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(objClass.getSimpleName());
            if(!isCloudStorage){
                parseQuery.fromLocalDatastore();
            }
            for(Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(constraintObj);
                if(value != null) {
                    parseQuery.whereEqualTo(field.getName(), value);
                }
            }
            List<ParseObject> parseObjects = parseQuery.find();
            Log.d(getClass().getName(), "Count " + parseObjects.size());
            for(ParseObject parseObject : parseObjects){
                if(isCloudStorage) {
                    parseObject.delete();
                } else {
                    parseObject.unpin();
                }
            }
            Log.d(getClass().getName(), " ========================================================= ");
        } catch (ParseException e){
            if(e.getCode() == 101){
                throw new DataNotFoundException();
            }
            throw e;
        }
    }


}
