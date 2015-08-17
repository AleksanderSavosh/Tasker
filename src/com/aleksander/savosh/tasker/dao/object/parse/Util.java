package com.aleksander.savosh.tasker.dao.object.parse;

import android.annotation.SuppressLint;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.model.object.*;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class Util {

    public static final Set<Class> CLASSES = new HashSet<Class>(Arrays.asList(
            new Class[] {String.class, Integer.class, Date.class}));
    public static final Set<Class> RELATIVE_CLASSES = new HashSet<Class>(Arrays.asList(
            new Class[] {Account.class, Phone.class, Notice.class, Property.class, List.class}));

    public static Class getListGenericType(Field field) {
        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
        return (Class) stringListType.getActualTypeArguments()[0];
    }

    public static Set<Field> getFieldsWithAccessible(Class clazz, Set<Class> classesType) {
        Set<Field> results = new HashSet<Field>();
        while(clazz != null){
            for(Field field : clazz.getDeclaredFields()){
                field.setAccessible(true);
                if(classesType.contains(field.getType())){
                    results.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return results;
    }


    public static boolean idIsEmpty(String id){
        return id == null || id.trim().length() == 0;
    }

    public static String getIdForLocalDb(Class clazz) throws ParseException {
        String id;
        do {
            id = "" + ((int) (Math.random() * 1000000));
        } while(!ParseQuery.getQuery(clazz.getSimpleName())
                .fromLocalDatastore()
                .whereEqualTo("objectId", id)
                .find().isEmpty());
        return id;
    }

    public static ParseObject getParseObjectById(String id, Class clazz, boolean isCloudMode) throws ParseException, OtherException, DataNotFoundException {

        //полезные параметры
        String clazzName = clazz.getSimpleName();

        //результат
        ParseObject parseObject;

        //если есть ид то тут два варианта, брать по ид из локальной базу или брать из облака
        if(isCloudMode){
            parseObject = ParseQuery.getQuery(clazzName).get(id);
        } else {
            List<ParseObject> parseObjects = ParseQuery.getQuery(clazzName)
                    .fromLocalDatastore().whereEqualTo("objectId", id).find();
            if(parseObjects.size() == 0){
                throw new DataNotFoundException("Not found in local data store object with id: " + id);
            } else if (parseObjects.size() == 1){
                parseObject = parseObjects.get(0);
            } else {
                throw new OtherException("Found is too many results in local data store object with id: " + id);
            }
        }
        return parseObject;
    }

    /**
     * Возвращаем ПарсОбжэкт для модели по id или создаем новый без зависимостей
     */
    public static ParseObject baseModelToParseObject(Base base, boolean isCloudMode) throws ParseException,
            IllegalAccessException, OtherException, DataNotFoundException {

        //полезные параметры
        Class clazz = base.getClass();
        String clazzName = clazz.getSimpleName();

        //результат
        ParseObject parseObject;

        // если ид нет тогда создаем парс объект
        if(idIsEmpty(base.getObjectId())){
            parseObject = ParseObject.create(clazzName);

        } else {

            //предположительно парс объект уже есть в хранилище
            parseObject = getParseObjectById(base.getObjectId(), clazz, isCloudMode);

        }

        //собсно переносим поля из модели в парс объект
        updateParseObjectByBaseModel(parseObject, base, isCloudMode);

        return parseObject;
    }


    public static void updateParseObjectByBaseModel(ParseObject parseObject, Base base, boolean isCloudMode) throws
            IllegalAccessException, ParseException {

        //полезные параметры
        Class clazz = base.getClass();
        String clazzName = clazz.getSimpleName();

        for(Field field : getFieldsWithAccessible(clazz, CLASSES)){
            String fieldName = field.getName();

            if(!isCloudMode && fieldName.equalsIgnoreCase("objectId") && idIsEmpty((String) field.get(base))){
                field.set(base, getIdForLocalDb(clazz));
            }

            if(!isCloudMode && fieldName.equalsIgnoreCase("createdAt") && field.get(base) == null){
                field.set(base, new Date());
            }

            if(!isCloudMode && fieldName.equalsIgnoreCase("updatedAt")){
                field.set(base, new Date());
            }

            if(isCloudMode && (fieldName.equalsIgnoreCase("objectId") ||
                    fieldName.equalsIgnoreCase("createdAt") ||
                    fieldName.equalsIgnoreCase("updatedAt"))){
                continue;
            }

            if(parseObject.has(fieldName)) {
                parseObject.remove(fieldName);
            }

            parseObject.put(fieldName, field.get(base));
            Object value = field.get(base);
            if(value != null) {
                parseObject.put(fieldName, value);
            }
        }
    }

    public static List<ParseObject> getRelationsParseObjects(
            ParseObject parent, Class childClazz, boolean isCloudStorage) throws ParseException {
        String childClazzName = childClazz.getName();
        if(isCloudStorage){
            return ParseQuery.getQuery(childClazz.getSimpleName())
                    .whereEqualTo(childClazzName, parent).find();
        } else {
            return ParseQuery.getQuery(childClazz.getSimpleName()).fromLocalDatastore()
                    .whereEqualTo(childClazzName, parent).find();
        }
    }

    public static void fillModelRelationsRec(Base base, ParseObject object, boolean isCloudStorage) throws
            ParseException, InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException {
        Class clazz = base.getClass();
        for(Field field : Util.getFieldsWithAccessible(clazz, Util.RELATIVE_CLASSES)){
            if(field.getType().equals(List.class)){
                Class childClazz = Util.getListGenericType(field);
                List<ParseObject> childrenParseObjects = getRelationsParseObjects(object, childClazz, isCloudStorage);
                List<Base> bases = new ArrayList<Base>();
                for(ParseObject childParseObject : childrenParseObjects){
                    Base childBase = Util.paresObjectToBaseModel(childParseObject, childClazz);
                    fillModelRelationsRec(childBase, childParseObject, isCloudStorage);
                    bases.add(childBase);
                }
                field.set(base, bases);
            }
        }
    }

    public static void updateModelByParseObject(Base base, ParseObject parseObject) throws IllegalAccessException {
        Class clazz = base.getClass();

        for(Field field : getFieldsWithAccessible(clazz, CLASSES)) {
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
            field.set(base, value);
        }
    }

    @SuppressWarnings("unchecked")
    public static Base paresObjectToBaseModel(ParseObject parseObject, Class clazz) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Base base = (Base) clazz.getConstructor().newInstance();
        updateModelByParseObject(base, parseObject);
        return base;
    }

//    public static Map<Integer, List<Relations>> getMapRelationsRec(
//            String s, Class clazz, boolean isCloudMode, Integer deep
//    ) throws ParseException, OtherException, DataNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        Map<Integer, List<Relations>> map = new HashMap<Integer, List<Relations>>();
//
//        Relations relations = new Relations();
//        relations.parentObject = getParseObjectById(s, clazz, isCloudMode);
//
//        map.put(deep, new ArrayList<Relations>(Arrays.asList(relations)));
//
//        for(Class childClazz : RELATIVE_CLASSES){
//            String key = childClazz.getSimpleName();
//            if(relations.parentObject.containsKey(key)){
//                List<ParseObject> objects;
//                if(isCloudMode){
//                    objects = ParseQuery.getQuery(key)
//                            .whereEqualTo(clazz.getSimpleName(), relations.parentObject).find();
//                } else {
//                    objects = ParseQuery.getQuery(key).fromLocalDatastore()
//                            .whereEqualTo(clazz.getSimpleName(), relations.parentObject).find();
//                }
//                if(relations.childObjects == null){
//                    relations.childObjects = new ArrayList<ParseObject>();
//                }
//                if(relations.children == null){
//                    relations.children = new ArrayList<Base>();
//                }
//                for(ParseObject object : objects){
//                    relations.childObjects.add(object);
//                    Base base = paresObjectToBaseModel(object, childClazz);
//                    relations.children.add(base);
//                    Map<Integer, List<Relations>> childMap = getMapRelationsRec(base.getObjectId(), childClazz,
//                            isCloudMode, deep + 1);
//
//                }
//            }
//        }
//        return map;
//    }


    public static class Relations {
        public Integer deep;

        public Base parent;
        public ParseObject parentObject;

        public List<Base> children;
        public List<ParseObject> childObjects;
    }

    public static void updateRelationsModels(Map<Integer, List<Relations>> map) throws IllegalAccessException,
            OtherException {
        for(Integer key : new TreeSet<Integer>(map.keySet()).descendingSet()){
            for(Util.Relations relations : map.get(key)) {
                if(relations.children != null) {
                    for (int i = 0; i < relations.children.size(); i++) {
                        updateModelByParseObject(relations.children.get(i), relations.childObjects.get(i));
                    }
                }
            }
        }
        if(map.get(1).size() == 1){
            updateModelByParseObject(map.get(1).get(1).parent, map.get(1).get(1).parentObject);
        } else {
            throw new OtherException();
        }
    }

    public static void saveRelationsParseObject(Map<Integer, List<Relations>> map, boolean isCloudStorage) throws
            ParseException, OtherException {
        for(Integer key : new TreeSet<Integer>(map.keySet()).descendingSet()){
            for(Util.Relations relations : map.get(key)) {
                if(relations.childObjects != null){
                    for (ParseObject childObject : relations.childObjects) {
                        if (isCloudStorage) {
                            childObject.save();
                        } else {
                            childObject.pin();
                        }
                    }
                }
            }
        }
        if(map.get(1).size() == 1){
            if(isCloudStorage) {
                map.get(1).get(0).parentObject.save();
            } else {
                map.get(1).get(0).parentObject.pin();
            }
        } else {
            throw new OtherException();
        }
    }

    public static void createRelations(Map<Integer, List<Relations>> map){
        for(Integer key : new TreeSet<Integer>(map.keySet()).descendingSet()){
            for(Util.Relations relations : map.get(key)) {
                if(relations.childObjects != null) {
                    for (ParseObject childObject : relations.childObjects) {
                        String childClassName = childObject.getClassName();
                        relations.parentObject.put(childClassName, childObject);
                    }
                }
            }
        }
    }

    public static void fillMapRelations(Map<Integer, List<Relations>> map, boolean isCloudStorage) throws
            DataNotFoundException, OtherException, ParseException, IllegalAccessException {
        for(Integer key : new TreeSet<Integer>(map.keySet()).descendingSet()){
            for(Util.Relations relations : map.get(key)) {
                if(relations.parentObject == null) {
                    relations.parentObject = Util.baseModelToParseObject(relations.parent, isCloudStorage);
                }
                if(relations.childObjects == null && relations.children != null) {
                    relations.childObjects = new ArrayList<ParseObject>();
                    for (Base child : relations.children) {
                        relations.childObjects.add(Util.baseModelToParseObject(child, isCloudStorage));
                    }
                }
            }
        }
    }

    @SuppressLint("UseSparseArrays")
    public static Map<Integer, List<Relations>> getMapRelationsRec(Base base, Integer deep) throws IllegalAccessException {
        Map<Integer, List<Relations>> map = new HashMap<Integer, List<Relations>>();

        Relations relations = getModelRelations(base);
        map.put(deep, new ArrayList<Relations>(Arrays.asList(relations)));

        if(relations.children != null) {
            for (Base child : relations.children) {
                Map<Integer, List<Relations>> childRelations = getMapRelationsRec(child, deep + 1);

                for (Integer key : childRelations.keySet()) {
                    if (map.containsKey(key)) {
                        map.get(key).addAll(childRelations.get(key));
                    } else {
                        map.put(key, childRelations.get(key));
                    }
                }
            }
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    public static Relations getModelRelations(Base base) throws IllegalAccessException {
        Class clazz = base.getClass();

        Relations relations = new Relations();
        relations.parent = base;

        for(Field field : getFieldsWithAccessible(clazz, RELATIVE_CLASSES)){
            if(field.get(base) == null){
                continue;
            }
            if(relations.children == null){
                relations.children = new ArrayList<Base>();
            }
            if(field.getType().equals(List.class)){
                for(Base relBase : (List<Base>) field.get(base)) {
                    relations.children.add(relBase);
                }
                continue;
            }
            relations.children.add((Base) field.get(base));
        }

        return relations;
    }



    private static void printFields(Set<Field> fields){
        for(Field field : fields){
            System.out.println(field);
        }
    }

    private static void printRelations(Map<Integer, List<Relations>> map){
        for(Integer key : new TreeSet<Integer>(map.keySet()).descendingSet()){
            for(Relations relations : map.get(key)) {
                for(Base child : relations.children) {
                    System.out.println("Deep: " + key + ", parent: " + relations.parent.getClass().getSimpleName() +
                            ", child: " + child.getClass().getSimpleName());
                }
            }
        }
    }


    public static void main(String[] args) throws IllegalAccessException {
//        printFields(getFieldsWithAccessible(Account.class, CLASSES));
//        printFields(getFieldsWithAccessible(Account.class, RELATIVE_CLASSES));
//        System.out.println(getModelRelationsRec(new Account()));

        printRelations(getMapRelationsRec(createRandomAccount(), 1));


    }

    private static List<Property> createRandomProperties(){
        List<Property> properties = new ArrayList<Property>();
        int count = 5;//(int) (Math.random() * 50);
        for(int i = 0; i < count; i++){
            properties.add(new Property(i, "Some text " + i, new Date()));
        }
        return properties;
    }

    private static List<Notice> createRandomNotices(){
        List<Notice> notices = new ArrayList<Notice>();
        int count = 5;//(int) (Math.random() * 50);
        for(int i = 0; i < count; i++){
            notices.add(new Notice(createRandomProperties()));
        }
        return notices;
    }

    private static List<Phone> createRandomPhones(){
        List<Phone> phones = new ArrayList<Phone>();
        int count = 5;// (int) (Math.random() * 50);
        for(int i = 0; i < count; i++){
            phones.add(new Phone("Number " + i + i + i + i + i + i));
        }
        return phones;
    }

    private static Account createRandomAccount(){
        return new Account("test one", createRandomPhones(), createRandomNotices());
    }

}
