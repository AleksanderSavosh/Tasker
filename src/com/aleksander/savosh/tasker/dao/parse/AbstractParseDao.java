package com.aleksander.savosh.tasker.dao.parse;

import android.util.Log;
import com.aleksander.savosh.tasker.StringUtil;
import com.aleksander.savosh.tasker.dao.BaseDao;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
            Log.d(getClass().getName(), " --- === CREATE " + (isCloudStorage?"":"LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + objClass.getSimpleName());
            Log.d(getClass().getName(), "INPUT: " + obj);

            ParseObject parseObject = new ParseObject(objClass.getSimpleName());
            for (Field field : ReflectionUtil.getAllFields(objClass)) {
                field.setAccessible(true);
                String name = field.getName();

                if(!isCloudStorage && name.equalsIgnoreCase("objectId") && StringUtil.isEmpty((String) field.get(obj))){
                    String id;
                    do {
                        id = "" + ((int) (Math.random() * 1000000));
                    } while(!ParseQuery.getQuery(objClass.getSimpleName())
                            .fromLocalDatastore()
                            .whereEqualTo("objectId", id)
                            .find().isEmpty());
                    field.set(obj, id);
                }

                if(!isCloudStorage && name.equalsIgnoreCase("createdAt") && field.get(obj) == null){
                    field.set(obj, new Date());
                }

                if(!isCloudStorage && name.equalsIgnoreCase("updatedAt") && field.get(obj) == null){
                    field.set(obj, new Date());
                }

                if(isCloudStorage && (name.equalsIgnoreCase("objectId") ||
                        name.equalsIgnoreCase("createdAt") ||
                        name.equalsIgnoreCase("updatedAt"))){
                    continue;
                }

                Object value = field.get(obj);
                if(value != null) {
                    Log.d(getClass().getName(),
                            "parseObject.put(" + name + ", " + value + "); " + "field type: " + field.getType());

                    parseObject.put(name, value);
                }
            }

            if(isCloudStorage) {
                parseObject.save();
            } else {
                parseObject.pin();
            }

            Obj result = transformer.fromParseObject(parseObject);
            Log.d(getClass().getName(), "RESULT: " + result);
            return result;
        } catch (ParseException e){
            if(e.getCode() == 101){
                throw new DataNotFoundException();
            }
            throw e;
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
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
        Log.d(getClass().getName(), " --- === READ " + (isCloudStorage?"":"LOCAL") + " === --- ");
        Log.d(getClass().getName(), "CLASS NAME: " + objClass.getSimpleName());
        Log.d(getClass().getName(), "INPUT: " + constraintObj);

        try {
            List<Obj> objList = new ArrayList<Obj>();
            for(ParseObject parseObject : findParseObjects(constraintObj)){
                objList.add(transformer.fromParseObject(parseObject));
            }
            Log.d(getClass().getName(), "RESULT: " + objList);
            return objList;
        } catch (ParseException e){
            if(e.getCode() == 101){
                throw new DataNotFoundException();
            }
            throw e;
        } finally {
            Log.d(getClass().getName(), " ============================================= ");
        }
    }

    private List<ParseObject> findParseObjects(Obj constraintObj) throws IllegalAccessException, ParseException {
        List<ParseObject> parseObjects = new ArrayList<ParseObject>();
        Map<String, Field> fieldMap = ReflectionUtil.fieldsListToMap(ReflectionUtil.getAllFields(objClass));
        Log.d(getClass().getName(), "OBJECT FIELD MAP: " + fieldMap.toString());

        //get by id
        Field objectIdField = fieldMap.get("objectId");
        objectIdField.setAccessible(true);
        String objectId = constraintObj != null ? (String) objectIdField.get(constraintObj) : null;
        if(objectId != null){
            Log.d(getClass().getName(), "FIND BY ID: " + objectId);
            if(isCloudStorage) {
                parseObjects.add(ParseQuery.getQuery(objClass.getSimpleName()).get(objectId));
            } else {
                ParseQuery<ParseObject> queryById = ParseQuery.getQuery(objClass.getSimpleName());
                queryById.fromLocalDatastore();
                parseObjects = queryById.whereEqualTo("objectId", objectId).find();
            }
        }

        //find other variants
        if(parseObjects.isEmpty()){
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(objClass.getSimpleName());
            if(!isCloudStorage){
                parseQuery.fromLocalDatastore();
            }
            Log.d(getClass().getName(), "FIND BY MAP:");
            for(Field field : fieldMap.values()) {
                String name = field.getName();
                if(name.equals("objectId") || name.equals("createdAt") || name.equals("updatedAt")){
                    continue;
                }
                field.setAccessible(true);
                Object value = constraintObj != null ? field.get(constraintObj) : null;
                if(value != null) {
                    Log.d(getClass().getName(), "..." + name + ":" + value);
                    parseQuery.whereEqualTo(name, value);
                }
            }
            parseObjects = parseQuery.find();
        }
        return parseObjects;
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
        Log.d(getClass().getName(), " --- === DELETE " + (isCloudStorage?"":"LOCAL") + " === --- ");
        Log.d(getClass().getName(), "CLASS NAME: " + objClass.getSimpleName());
        Log.d(getClass().getName(), "INPUT: " + constraintObj);
        try {
            if(constraintObj == null){
                return;
            }

            List<ParseObject> parseObjects = findParseObjects(constraintObj);
            Log.d(getClass().getName(), "Count " + parseObjects.size());
            for(ParseObject parseObject : parseObjects){
                if(isCloudStorage) {
                    parseObject.delete();
                } else {
                    parseObject.unpin();
                }
            }
        } catch (ParseException e){
            if(e.getCode() == 101){
                throw new DataNotFoundException();
            }
            throw e;
        } finally {
            Log.d(getClass().getName(), " ========================================================= ");
        }
    }
}
