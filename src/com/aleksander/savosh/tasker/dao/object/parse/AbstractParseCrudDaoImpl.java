package com.aleksander.savosh.tasker.dao.object.parse;

import android.annotation.SuppressLint;
import android.util.Log;
import com.aleksander.savosh.tasker.StringUtil;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.CannotCreateException;
import com.aleksander.savosh.tasker.dao.object.CrudDao;
import com.aleksander.savosh.tasker.dao.ReflectionUtil;
import com.aleksander.savosh.tasker.model.object.Base;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;


public class AbstractParseCrudDaoImpl<Model extends Base> implements CrudDao<Model, String> {

    private final boolean isCloudStorage;
    private final Class<Model> clazz;

    public AbstractParseCrudDaoImpl(boolean isCloudStorage, Class<Model> clazz) {
        this.isCloudStorage = isCloudStorage;
        this.clazz = clazz;
    }

    @Override
    public Model create(Model model) {
        return null;
    }

    @Override
    public Model read(String s) {
        return null;
    }

    @Override
    public Model update(Model model) {
        return null;
    }

    @Override
    public boolean delete(String s) {
        return false;
    }

    @Override
    public Model createWithRelations(Model model) {
        return null;
    }

    @Override
    public Model readWithRelations(String s) {
        return null;
    }

    @Override
    public Model updateWithRelations(Model model) {
        return null;
    }

    @Override
    public boolean deleteWithRelations(String s) {
        return false;
    }

    @Override
    public Model createThrowException(Model model) throws CannotCreateException {
        Class clazz = model.getClass();
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === CREATE " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            ParseObject parseObject = new ParseObject(className);
            for (Field field : ReflectionUtil.getAllFieldsWithoutObjects(clazz)) {
                field.setAccessible(true);
                String name = field.getName();

                if(!isCloudStorage && name.equalsIgnoreCase("objectId") && StringUtil.isEmpty((String) field.get(model))){
                    String id;
                    do {
                        id = "" + ((int) (Math.random() * 1000000));
                    } while(!ParseQuery.getQuery(className)
                            .fromLocalDatastore()
                            .whereEqualTo("objectId", id)
                            .find().isEmpty());
                    field.set(model, id);
                }

                if(!isCloudStorage && name.equalsIgnoreCase("createdAt") && field.get(model) == null){
                    field.set(model, new Date());
                }

                if(!isCloudStorage && name.equalsIgnoreCase("updatedAt") && field.get(model) == null){
                    field.set(model, new Date());
                }

                if(isCloudStorage && (name.equalsIgnoreCase("objectId") ||
                        name.equalsIgnoreCase("createdAt") ||
                        name.equalsIgnoreCase("updatedAt"))){
                    continue;
                }

                Object value = field.get(model);
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

            Model result = null; //transformer.fromParseObject(parseObject);
            Log.d(getClass().getName(), "RESULT: " + result);
            return result;
        } catch (Exception e){
            throw new CannotCreateException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    public Model readThrowException(String s) throws DataNotFoundException {
        return null;
    }

    @Override
    public Model updateThrowException(Model model) throws DataNotFoundException {
        return null;
    }

    @Override
    public boolean deleteThrowException(String s) throws DataNotFoundException {
        return false;
    }

    @SuppressWarnings("unchecked")
    private ParseObject buildParseObject(Base model, Map<Integer, List<ParseObject>> listMap, Integer deep) throws ParseException, IllegalAccessException {
        Class clazz = model.getClass();
        String className = clazz.getSimpleName();
        ParseObject parseObject = new ParseObject(className);

        for (Field field : ReflectionUtil.getAllFieldsWithoutObjects(clazz)) {
            field.setAccessible(true);
            String name = field.getName();

            if(!isCloudStorage && name.equalsIgnoreCase("objectId") && StringUtil.isEmpty((String) field.get(model))){
                String id;
                do {
                    id = "" + ((int) (Math.random() * 1000000));
                } while(!ParseQuery.getQuery(className)
                        .fromLocalDatastore()
                        .whereEqualTo("objectId", id)
                        .find().isEmpty());
                field.set(model, id);
            }

            if(!isCloudStorage && name.equalsIgnoreCase("createdAt") && field.get(model) == null){
                field.set(model, new Date());
            }

            if(!isCloudStorage && name.equalsIgnoreCase("updatedAt") && field.get(model) == null){
                field.set(model, new Date());
            }

            if(isCloudStorage && (name.equalsIgnoreCase("objectId") ||
                    name.equalsIgnoreCase("createdAt") ||
                    name.equalsIgnoreCase("updatedAt"))){
                continue;
            }

            Object value = field.get(model);
            if(value != null) {
                Log.d(getClass().getName(),
                        "parseObject.put(" + name + ", " + value + "); " + "field type: " + field.getType());

                parseObject.put(name, value);
            }
        }

        for (Field field : ReflectionUtil.getAllFieldsObjects(clazz)) {
            field.setAccessible(true);
            Object value = field.get(model);

            if(field.getType().equals(List.class)){ //list

                for(Base base : (List<Base>) value) {
                    ParseObject objectBase = buildParseObject(base, listMap, deep + 1);
                    objectBase.put(parseObject.getClassName(), parseObject);
                }

            } else {// object
                ParseObject objectBase = buildParseObject((Base) value, listMap, deep + 1);
                objectBase.put(parseObject.getClassName(), parseObject);
            }
        }

        if(listMap.containsKey(deep)){
            listMap.get(deep).add(parseObject);
        } else {
            listMap.put(deep, new ArrayList<ParseObject>(Arrays.asList(parseObject)));
        }

        return parseObject;
    }



    @Override
    @SuppressWarnings("unchecked")
    @SuppressLint("UseSparseArrays")
    public Model createWithRelationsThrowException(Model model) throws CannotCreateException {
        Class clazz = model.getClass();
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === CREATE " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            Map<Integer, List<ParseObject>> listMap = new HashMap<Integer, List<ParseObject>>();

            ParseObject parseObject = buildParseObject(model, listMap, 1);

            Set<Integer> set = new TreeSet<Integer>(listMap.keySet()).descendingSet();
            for(Integer key : set){
                Log.d(getClass().getName(), "SAVE DEEP LVL: " + key);
                for(ParseObject object : listMap.get(key)){
                    Log.d(getClass().getName(), "SAVE CLASS: " + object.getClassName());
                    if(isCloudStorage) {
                        object.save();
                    } else {
                        object.pin();
                    }
                }
            }

            return null;//readWithRelationsThrowException(parseObject.getObjectId());
        } catch (Exception e){
            throw new CannotCreateException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Base> T buildModel(ParseObject parentParseObject, Class<T> parentClazz) throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException, ParseException {
        Log.d(getClass().getName(), "--- Transform from parse object --- ");

        Constructor<T> constructor = parentClazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T parentModel = constructor.newInstance();

        for(Field field : ReflectionUtil.getAllFieldsWithoutObjects(parentClazz)) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = parentParseObject.get(fieldName);
            if(value == null){
                if(fieldName.equals("objectId")){
                    value = parentParseObject.getObjectId();
                }
                if(fieldName.equals("createdAt")){
                    value = parentParseObject.getCreatedAt();
                }
                if(fieldName.equals("updatedAt")){
                    value = parentParseObject.getUpdatedAt();
                }
            }
            Log.d(getClass().getName(), "field set: name: " + fieldName + " value: " + value);
            field.set(parentModel, value);
        }
        Log.d(getClass().getName(), "-------------------------------");


        for(Field parentField : ReflectionUtil.getAllFieldsObjects(parentClazz)){
            parentField.setAccessible(true);

            if(parentField.getType().equals(List.class)){ //list

                ParameterizedType stringListType = (ParameterizedType) parentField.getGenericType();
                Class<T> childClazz = (Class<T>) stringListType.getActualTypeArguments()[0];
                String childClazzName = childClazz.getSimpleName();

                ParseQuery query = ParseQuery.getQuery(childClazzName);
                if(!isCloudStorage){
                    query.fromLocalDatastore();
                }
                query.whereEqualTo(parentClazz.getSimpleName(), parentParseObject);
                List<ParseObject> childParseObjects = query.find();

                List<T> list = new ArrayList<T>();
                for(ParseObject childParseObject : childParseObjects) {
                    list.add(buildModel(childParseObject, childClazz));
                }
                parentField.set(parentModel, list);

            } else {// object

                Class childClazz = parentField.getType();
                String childClazzName = childClazz.getSimpleName();

                ParseQuery query = ParseQuery.getQuery(childClazzName);
                if(!isCloudStorage){
                    query.fromLocalDatastore();
                }
                query.whereEqualTo(parentClazz.getSimpleName(), parentParseObject);
                List<ParseObject> childParseObjects = query.find();


                if(childParseObjects.size() > 1){
                    throw new RuntimeException("Some error. Contact your administrator.");
                }

                ParseObject childParseObject = childParseObjects.get(0);
                parentField.set(parentModel, buildModel(childParseObject, childClazz));

            }
        }

        return parentModel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model readWithRelationsThrowException(String s) throws DataNotFoundException {
        String className = clazz.getSimpleName();
        Log.d(getClass().getName(), " --- === READ " + (isCloudStorage?"":"LOCAL") + " === --- ");
        Log.d(getClass().getName(), "CLASS NAME: " + className);
        Log.d(getClass().getName(), "INPUT ID: " + s);

        try {
            ParseObject parseObject = ParseQuery.getQuery(className).get(s);
            return buildModel(parseObject, clazz);
        } catch (ParseException e){
            if(e.getCode() == 101){
                throw new DataNotFoundException(e.getMessage(), e);
            }
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================= ");
        }
    }

    @Override
    public Model updateWithRelationsThrowException(Model model) throws DataNotFoundException {
        return null;
    }

    @Override
    public boolean deleteWithRelationsThrowException(String s) throws DataNotFoundException {
        return false;
    }
}
