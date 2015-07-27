package com.aleksander.savosh.tasker.model.alternative;

import android.util.Log;
import com.aleksander.savosh.tasker.model.BaseModel;
import com.aleksander.savosh.tasker.service.*;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AlternativeParseDaoImpl<Obj> implements BaseDao<Obj> {

    private final Class<Obj> objClass;
    private final ParseTransformer<Obj> transformer;
    private final boolean isCloudStorage;
    private final Set<Class> availClasses;

    public AlternativeParseDaoImpl(Class<Obj> objClass, boolean isCloudStorage, Set<Class> availClasses) {
        this.objClass = objClass;
        this.transformer = new ParseTransformer<Obj>(objClass);
        this.isCloudStorage = isCloudStorage;
        this.availClasses = availClasses;
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
    public Obj createThrowExceptions(Obj obj) throws Exception {
        try {
            Log.d(getClass().getName(), " - - - = = = CREATE OBJECT = = = - - -");
            Log.d(getClass().getName(), isCloudStorage?"CLOUD MODE":"LOCAL MODE");
            Log.d(getClass().getName(), "obj -> " + obj);

            ParseObject object = createParseObject(obj);

            Log.d(getClass().getName(), "RESULT -> " + ParseUtil.toString(object));
            Log.d(getClass().getName(), " - - - = = = ============= = = = - - -");
            return null;
        } catch (ParseException e){
            if(e.getCode() == 101){
                throw new DataNotFoundException();
            }
            throw e;
        }
    }

    private ParseObject createParseObject(Object obj) throws IllegalAccessException, ParseException {
        List<Field> collections = new ArrayList<Field>();
        List<Field> objects = new ArrayList<Field>();

        ParseObject parseObject = ParseObject.create(obj.getClass().getName());

        for(Field field : ReflectionUtil.getAllFields(objClass)){
            String name = field.getName();
            Object value = field.get(obj);

            if(value instanceof Collection){
                for(Object o : (Collection) value){
                    parseObject.put(name, createParseObject(o));
                }
                continue;
            }

            if(availClasses.contains(value.getClass())){
                parseObject.put(name, createParseObject(value));
                continue;
            }

            parseObject.put(name, value);
        }

        if(isCloudStorage){
            parseObject.save();
        } else {
            parseObject.pin();
        }

        return parseObject;
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

            List<ParseObject> parseObjects = new ArrayList<ParseObject>();
            Map<String, Field> fieldMap = ReflectionUtil.fieldsListToMap(ReflectionUtil.getAllFields(objClass));

            //get by id
            Field objectIdField = fieldMap.get("objectId");
            objectIdField.setAccessible(true);
            String objectId = constraintObj != null ? (String) objectIdField.get(constraintObj) : null;
            if(objectId != null){
                if(isCloudStorage) {
                    parseObjects.add(ParseQuery.getQuery(objClass.getSimpleName()).get(objectId));
                } else {
                    parseObjects = ParseQuery.getQuery(objClass.getSimpleName())
                            .whereEqualTo("objectId", objectId).find();
                }
            }

            //find other variants
            if(parseObjects.isEmpty()){
                for(Field field : fieldMap.values()) {
                    field.setAccessible(true);
                    Object value = constraintObj != null ? field.get(constraintObj) : null;
                    if(value != null) {
                        parseQuery.whereEqualTo(field.getName(), value);
                    }
                }
                parseObjects = parseQuery.find();
            }

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
