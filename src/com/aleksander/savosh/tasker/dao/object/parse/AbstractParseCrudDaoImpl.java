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

    private static final String DEFAULT_ERROR_MSG = "Some error. Contact your administrator.";

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
    @SuppressWarnings("unchecked")
    public Model createThrowException(Model model) throws CannotCreateException {
        Class clazz = model.getClass();
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === CREATE " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            ParseObject parseObject = new ParseObject(className);
            for (Field field : getFieldsWithSuper(clazz)) {
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

            Model result = (Model) buildModel(parseObject, clazz, false);
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
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === READ " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + s);

            ParseObject object;
            if(isCloudStorage){
                object = ParseQuery.getQuery(className).get(s);
            } else {
                List<ParseObject> list = ParseQuery.getQuery(className)
                        .fromLocalDatastore()
                        .whereEqualTo("ObjectId", s)
                        .find();
                if(list.size() > 1){
                    throw new RuntimeException(DEFAULT_ERROR_MSG);
                }
                if(list.size() == 0){
                    throw new DataNotFoundException();
                }
                object = list.get(0);
            }

            return buildModel(object, clazz, false);
        } catch(ParseException e){
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException();
            }
            throw new RuntimeException(DEFAULT_ERROR_MSG);
        } catch (Exception e) {
            throw new RuntimeException(DEFAULT_ERROR_MSG);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    public Model updateThrowException(Model model) throws DataNotFoundException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === UPDATE " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            ParseObject object;
            if(isCloudStorage){
                object = ParseQuery.getQuery(className).get(model.getObjectId());
            } else {
                List<ParseObject> list = ParseQuery.getQuery(className)
                        .fromLocalDatastore()
                        .whereEqualTo("ObjectId", model.getObjectId())
                        .find();
                if(list.size() > 1){
                    throw new RuntimeException(DEFAULT_ERROR_MSG);
                }
                if(list.size() == 0){
                    throw new DataNotFoundException();
                }
                object = list.get(0);
            }
            return null;
        } catch(ParseException e){
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException();
            }
            throw new RuntimeException(DEFAULT_ERROR_MSG);
        } catch (Exception e) {
            throw new RuntimeException(DEFAULT_ERROR_MSG);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @Override
    public boolean deleteThrowException(String s) throws DataNotFoundException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === DELETE " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + s);

            ParseObject object = ParseQuery.getQuery(className).get(s);

            if(isCloudStorage) {
                object.delete();
            } else {
                object.unpin();
            }
            return true;
        } catch(ParseException e){
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                throw new DataNotFoundException();
            }
            throw new RuntimeException(DEFAULT_ERROR_MSG);
        }
    }

    @SuppressWarnings("unchecked")
    private ParseObject buildParseObject(Base model, Map<Integer, List<ParseObject>> listMap, Integer deep) throws ParseException, IllegalAccessException {
        Class clazz = model.getClass();
        String className = clazz.getSimpleName();
        ParseObject parseObject = new ParseObject(className);

        for (Field field : getFieldsWithSuper(clazz)) {
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

        for (Field field : getObjectsFieldsWithSuper(clazz)) {
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

    @SuppressWarnings("unchecked")
    private Map<String, Class<? extends Base>> findAllClasses(Class<? extends Base> clazz){
        Map<String, Class<? extends Base>> classes = new HashMap<String, Class<? extends Base>>();
        classes.put(clazz.getSimpleName(), clazz);
        for(Field field : getObjectsFieldsWithSuper(clazz)){
            if(field.getType().equals(List.class)){
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class<Base> childClazz = (Class<Base>) stringListType.getActualTypeArguments()[0];
                classes.putAll(findAllClasses(childClazz));
            } else {
                classes.putAll(findAllClasses((Class<Base>) field.getType()));
            }
        }
        return classes;
    }



    @SuppressWarnings("unchecked")
    private <T extends Base> void buildModelRelations(T model, Map<Class<? extends Base>, List<Base>> modelListMap) throws IllegalAccessException {

        for(Field field : getObjectsFieldsWithSuper(model.getClass())){
            field.setAccessible(true);

            if(field.getType().equals(List.class)){
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class<Base> childClazz = (Class<Base>) stringListType.getActualTypeArguments()[0];

                for(Base base : modelListMap.get(childClazz)){
                    buildModelRelations(base, modelListMap);
                }

                field.set(model, modelListMap.get(childClazz));
            } else {
                Class clazz = field.getType();
                if(modelListMap.get(clazz).size() > 1){
                    throw new RuntimeException(DEFAULT_ERROR_MSG);
                }

                buildModelRelations(modelListMap.get(clazz).get(0), modelListMap);
                field.set(model, modelListMap.get(clazz).get(0));
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @SuppressLint("UseSparseArrays")
    public Model createWithRelationsThrowException(Model model) throws CannotCreateException {
        Class clazz = model.getClass();
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === CREATE WITH RELATIONS " + (isCloudStorage ? "" : "LOCAL") + " === ----");
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

            Map<Class<? extends Base>, List<Base>> modelsMap =
                    new HashMap<Class<? extends Base>, List<Base>>();
            Map<String, Class<? extends Base>> classes = findAllClasses(clazz);

            for(Integer key : listMap.keySet()){
                for(ParseObject object : listMap.get(key)){
                    Class<? extends Base> objectClazz = classes.get(object.getClassName());
                    Base base = buildModel(object, objectClazz, false);

                    if(modelsMap.containsKey(objectClazz)){
                        modelsMap.get(objectClazz).add(base);
                    } else {
                        modelsMap.put(objectClazz, new ArrayList<Base>(Arrays.asList(base)));
                    }
                }
            }

            Model resultModel = (Model) buildModel(parseObject, clazz, false);
            buildModelRelations(resultModel, modelsMap);
            return resultModel;
        } catch (Exception e){
            throw new CannotCreateException(e.getMessage(), e);
        } finally {
            Log.d(getClass().getName(), " ============================================ ");
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Base> T buildModel(ParseObject parentParseObject, Class<T> parentClazz, boolean withRelations) throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException, ParseException {
        Log.d(getClass().getName(), "--- Transform from parse object --- ");

        Constructor<T> constructor = parentClazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T parentModel = constructor.newInstance();

        for(Field field : getFieldsWithSuper(parentClazz)) {
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

        if(withRelations) {
            for (Field parentField : getObjectsFieldsWithSuper(parentClazz)) {
                parentField.setAccessible(true);
                if (parentField.getType().equals(List.class)) { //list
                    ParameterizedType stringListType = (ParameterizedType) parentField.getGenericType();
                    Class<T> childClazz = (Class<T>) stringListType.getActualTypeArguments()[0];
                    String childClazzName = childClazz.getSimpleName();

                    ParseQuery query = ParseQuery.getQuery(childClazzName);
                    if (!isCloudStorage) {
                        query.fromLocalDatastore();
                    }
                    query.whereEqualTo(parentClazz.getSimpleName(), parentParseObject);
                    List<ParseObject> childParseObjects = query.find();

                    List<T> list = new ArrayList<T>();
                    for (ParseObject childParseObject : childParseObjects) {
                        list.add(buildModel(childParseObject, childClazz, true));
                    }
                    parentField.set(parentModel, list);
                } else {// object
                    Class childClazz = parentField.getType();
                    String childClazzName = childClazz.getSimpleName();

                    ParseQuery query = ParseQuery.getQuery(childClazzName);
                    if (!isCloudStorage) {
                        query.fromLocalDatastore();
                    }
                    query.whereEqualTo(parentClazz.getSimpleName(), parentParseObject);
                    List<ParseObject> childParseObjects = query.find();

                    if (childParseObjects.size() > 1) {
                        throw new RuntimeException(DEFAULT_ERROR_MSG);
                    }
                    ParseObject childParseObject = childParseObjects.get(0);
                    parentField.set(parentModel, buildModel(childParseObject, childClazz, true));
                }
            }
        }
        return parentModel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Model readWithRelationsThrowException(String s) throws DataNotFoundException {
        String className = clazz.getSimpleName();
        Log.d(getClass().getName(), " --- === READ WITH RELATIONS " + (isCloudStorage?"":"LOCAL") + " === --- ");
        Log.d(getClass().getName(), "CLASS NAME: " + className);
        Log.d(getClass().getName(), "INPUT ID: " + s);

        try {
            ParseObject parseObject = ParseQuery.getQuery(className).get(s);
            return buildModel(parseObject, clazz, true);
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

    @SuppressWarnings("unchecked")
    public Map<String, Base> getRecursivelyModelsFromModel(Base parentModel) throws IllegalAccessException {
        Map<String, Base> map = new HashMap<String, Base>();
        map.put(parentModel.getObjectId(), parentModel);
        for(Field field : getObjectsFieldsWithSuper(parentModel.getClass())){
            field.setAccessible(true);
            if(field.getType().equals(List.class)){
                for(Base base : (List<Base>) field.get(parentModel)){
                    map.putAll(getRecursivelyModelsFromModel(base));
                }
            } else {
                map.putAll(getRecursivelyModelsFromModel((Base) field.get(parentModel)));
            }
        }
        return map;
    }

    @Override
    public Model updateWithRelationsThrowException(Model model) throws DataNotFoundException {
        try {
            String className = clazz.getSimpleName();
            Log.d(getClass().getName(), " --- === UPDATE WITH RELATIONS " + (isCloudStorage ? "" : "LOCAL") + " === --- ");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + model);

            Map<String, Base> mapModels = getRecursivelyModelsFromModel(model); // что если в модели будет две новых, какие у них ид
            Map<String, ParseObject> mapParseObject = getRecursivelyParseObjects(model.getObjectId(), clazz);

            // 1. создать объекты которых нет а облаке
            // 2. удалить объекты которых нет в модели но которые есть облаке
            // 3. обновить остальные объекты





            return null;
        } catch(ParseException e){
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                throw new DataNotFoundException(e.getMessage(), e);
            }
            throw new RuntimeException(DEFAULT_ERROR_MSG);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(DEFAULT_ERROR_MSG);
        }
    }

    @Override
    public boolean deleteWithRelationsThrowException(String s) throws DataNotFoundException {
        String className = clazz.getSimpleName();
        try {
            Log.d(getClass().getName(), " --- === DELETE WITH RELATIONS " + (isCloudStorage ? "" : "LOCAL") + " === ----");
            Log.d(getClass().getName(), "CLASS NAME: " + className);
            Log.d(getClass().getName(), "INPUT: " + s);

            Map<String, ParseObject> map = getRecursivelyParseObjects(s, clazz);
            for(ParseObject object : map.values()) {
                if (isCloudStorage) {
                    object.delete();
                } else {
                    object.unpin();
                }
            }
            return true;
        } catch(ParseException e){
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                throw new DataNotFoundException(e.getMessage(), e);
            }
            throw new RuntimeException(DEFAULT_ERROR_MSG, e);
        } finally {
            Log.d(getClass().getName(), " ============================================= ");
        }
    }

    private Map<String, ParseObject> getRecursivelyParseObjects(String id, Class clazz) throws ParseException, DataNotFoundException {
        ParseObject object;
        if(!isCloudStorage){
            List<ParseObject> parseObjects = ParseQuery
                    .getQuery(clazz.getSimpleName())
                    .fromLocalDatastore()
                    .whereEqualTo("ObjectId", id)
                    .find();
            if(parseObjects.size() != 1){
                throw new DataNotFoundException();
            }
            object = parseObjects.get(0);
        } else {
            object = ParseQuery.getQuery(clazz.getSimpleName()).get(id);
        }
        Map<String, ParseObject> map = getRecursivelyParseObjects(object, clazz);
        map.put(id, object);
        return map;
    }

    @SuppressWarnings("unchecked")
    private Map<String, ParseObject> getRecursivelyParseObjects(ParseObject parentParseObject, Class clazz) throws ParseException {
        Map<String, ParseObject> map = new HashMap<String, ParseObject>();
        for(Field field : getObjectsFieldsWithSuper(clazz)){
            field.setAccessible(true);

            Class childClass;
            if(field.getType().equals(List.class)){
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                childClass = (Class) stringListType.getActualTypeArguments()[0];
            } else {
                childClass  = field.getType();
            }

            ParseQuery query = ParseQuery.getQuery(childClass.getSimpleName());
            if(!isCloudStorage){
                query.fromLocalDatastore();
            }
            List<ParseObject> childrenParseObject = query.whereEqualTo(clazz.getSimpleName(), parentParseObject).find();

            if(!field.getType().equals(List.class)){
                if(childrenParseObject.size() > 1){
                    throw new RuntimeException(DEFAULT_ERROR_MSG);
                }
            }
            for(ParseObject childParseObject : childrenParseObject){
                if(isCloudStorage){
                    map.put(childParseObject.getObjectId(), childParseObject);
                } else {
                    map.put(childParseObject.getString("ObjectId"), childParseObject);
                }
                map.putAll(getRecursivelyParseObjects(childParseObject, childClass));
            }
        }
        return map;
    }

    public static List<Field> getFieldsWithSuper(Class objClass){
        Set<Field> fieldSet = new HashSet<Field>();
        Class temp = objClass;
        while(temp != null && temp != Object.class) {
            fieldSet.addAll(Arrays.asList(temp.getFields()));
            fieldSet.addAll(Arrays.asList(temp.getDeclaredFields()));
            temp = temp.getSuperclass();
        }

        Iterator<Field> fieldIterator = fieldSet.iterator();
        while (fieldIterator.hasNext()){
            Field field = fieldIterator.next();
            if(field.getType().equals(List.class)){
                fieldIterator.remove();
            }
            if(field.getType().getSuperclass() != null && field.getType().getSuperclass().equals(Base.class)){
                fieldIterator.remove();
            }
        }

        return new ArrayList<Field>(fieldSet);
    }

    public static List<Field> getObjectsFieldsWithSuper(Class objClass){
        Set<Field> fieldSet = new HashSet<Field>();
        Class temp = objClass;
        while(temp != null && temp != Object.class) {
            fieldSet.addAll(Arrays.asList(temp.getFields()));
            fieldSet.addAll(Arrays.asList(temp.getDeclaredFields()));
            temp = temp.getSuperclass();
        }

        Iterator<Field> fieldIterator = fieldSet.iterator();
        while (fieldIterator.hasNext()){
            Field field = fieldIterator.next();
            if(field.getType().equals(List.class)){
                continue;
            }
            if(field.getType().getSuperclass() != null && field.getType().getSuperclass().equals(Base.class)){
                continue;
            }
            fieldIterator.remove();
        }

        return new ArrayList<Field>(fieldSet);
    }
}
