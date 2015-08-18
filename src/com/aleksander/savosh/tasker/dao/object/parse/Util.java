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

    public static ParseObject createPO(Class clazz){
        return ParseObject.create(clazz.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public static Base createModel(Class clazz) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return (Base) clazz.getConstructor().newInstance();
    }

    public static ParseObject getPO(Class clazz, String id, boolean isCloudMode) throws ParseException,
            DataNotFoundException, OtherException {
        if(isCloudMode){
            return ParseQuery.getQuery(clazz.getSimpleName()).get(id);
        } else {
            List<ParseObject> objects = ParseQuery.getQuery(clazz.getSimpleName()).whereEqualTo("objectId", id).find();
            if(objects.size() == 1){
                return objects.get(0);
            } else if(objects.size() == 0){
                throw new DataNotFoundException();
            } else {
                throw new OtherException();
            }
        }
    }

    public static void setModel2PO(Base base, ParseObject po, boolean isCloudMode) throws ParseException,
            IllegalAccessException {

        Class clazz = base.getClass();

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

            if(po.has(fieldName)) {
                po.remove(fieldName);
            }

            po.put(fieldName, field.get(base));
            Object value = field.get(base);
            if(value != null) {
                po.put(fieldName, value);
            }
        }
    }

    public static void setPO2Model(ParseObject po, Base base) throws IllegalAccessException {
        Class clazz = base.getClass();

        for(Field field : getFieldsWithAccessible(clazz, CLASSES)) {
            String fieldName = field.getName();
            Object value = po.get(fieldName);
            if(value == null){
                if(fieldName.equals("objectId")){
                    value = po.getObjectId();
                }
                if(fieldName.equals("createdAt")){
                    value = po.getCreatedAt();
                }
                if(fieldName.equals("updatedAt")){
                    value = po.getUpdatedAt();
                }
            }
            field.set(base, value);
        }
    }

    public static void savePO(ParseObject po, boolean isCloudMode) throws ParseException {
        if(isCloudMode) {
            po.save();
        } else {
            po.pin();
        }
    }

    public static void deletePO(ParseObject po, boolean isCloudMode) throws ParseException {
        if(isCloudMode) {
            po.delete();
        } else {
            po.unpin();
        }
    }


    ///////////////////////////////////////////////////////////////////// RELATIONS

    public static class ModelPO {
        public ParseObject po;
        public Base base;

        @Override
        public String toString() {
            return "ModelPO{" +
                    "po=" + po +
                    ", base=" + base +
                    '}';
        }
    }

    public static class ModelPONode {
        public Integer deep;
        public ModelPO modelPO;
        public List<ModelPONode> nodes = new ArrayList<ModelPONode>();

        @Override
        public String toString() {
            return "ModelPONode{" +
                    "deep=" + deep +
                    ", modelPO=" + modelPO +
                    ", nodes=" + nodes +
                    '}';
        }
    }

    public static ModelPONode getNode(Class clazz, Base base, ParseObject po, boolean isCreateMode, boolean isCloudMode) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            ParseException, OtherException, DataNotFoundException {
        ModelPONode node = new ModelPONode();
        node.modelPO = new ModelPO();
        node.modelPO.base = base;
        node.modelPO.po = po;
        if(base == null && po != null){
            node.modelPO.base = createModel(clazz);
            setPO2Model(po, node.modelPO.base);
        }
        if(base != null && po == null){
            node.modelPO.po = isCreateMode ? createPO(clazz) : getPO(clazz, base.getObjectId(), isCloudMode);
            setModel2PO(base, node.modelPO.po, isCloudMode);
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    public static List<ParseObject> getRelationsPO(ParseObject parentPo, Class childClazz, boolean isCloudMode) throws
            ParseException {
        ParseQuery query = ParseQuery.getQuery(childClazz.getSimpleName());
        if(!isCloudMode){
            query.fromLocalDatastore();
        }
        query.whereEqualTo(parentPo.getClassName(), parentPo);
        return query.find();
    }

    public static void mergeMaps(Map<Integer, List<ModelPONode>> to, Map<Integer, List<ModelPONode>> from){
        for(Integer key : from.keySet()){
            if(to.containsKey(key)){
                to.get(key).addAll(from.get(key));
            } else {
                to.put(key, from.get(key));
            }
        }
    }

    @SuppressLint("UseSparseArrays")
    @SuppressWarnings("unchecked")
    public static Map<Integer, List<ModelPONode>> getModelPoTreeRec(
            Class clazz, Integer deep, Base base, ParseObject po, boolean isCreateMode, boolean isCloudMode) throws
            InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            ParseException, OtherException, DataNotFoundException {
        Map<Integer, List<ModelPONode>> map = new HashMap<Integer, List<ModelPONode>>();

        ModelPONode node = getNode(clazz, base, po, isCreateMode, isCloudMode);
        node.deep = deep;

        map.put(deep, new ArrayList<ModelPONode>(Arrays.asList(node)));

        for(Field field : getFieldsWithAccessible(clazz, RELATIVE_CLASSES)){
            if(field.getType().equals(List.class)){
                Class childClazz = getListGenericType(field);
                if(base == null && po != null){
                    for(ParseObject childPo : getRelationsPO(po, childClazz, isCloudMode)) {
                        Map<Integer, List<ModelPONode>> childMap =
                                getModelPoTreeRec(childClazz, deep + 1, null, childPo, isCreateMode, isCloudMode);

                        if(childMap.containsKey(deep + 1)){
                            node.nodes.addAll(childMap.get(deep + 1));
                        }
                        mergeMaps(map, childMap);
                    }
                }
                if(base != null && po == null){
                    for(Base childBase : (List<Base>) field.get(base)){
                        Map<Integer, List<ModelPONode>> childMap =
                                getModelPoTreeRec(childClazz, deep + 1, childBase, null, isCreateMode, isCloudMode);

                        if(childMap.containsKey(deep + 1)){
                            node.nodes.addAll(childMap.get(deep + 1));
                        }
                        mergeMaps(map, childMap);
                    }
                }
            } else {
                Class childClazz = getListGenericType(field);
                if(base == null && po != null){
                    for(ParseObject childPo : getRelationsPO(po, childClazz, isCloudMode)) {

                        Map<Integer, List<ModelPONode>> childMap =
                                getModelPoTreeRec(childClazz, deep + 1, null, childPo, isCreateMode, isCloudMode);

                        if(childMap.containsKey(deep + 1)){
                            node.nodes.addAll(childMap.get(deep + 1));
                        }

                        mergeMaps(map, childMap);

                    }
                }
                if(base != null && po == null){
                    Base childBase = (Base) field.get(base);
                    Map<Integer, List<ModelPONode>> childMap =
                            getModelPoTreeRec(childClazz, deep + 1, childBase, null, isCreateMode, isCloudMode);

                    if(childMap.containsKey(deep + 1)){
                        node.nodes.addAll(childMap.get(deep + 1));
                    }
                    mergeMaps(map, childMap);
                }

            }
        }

        return map;
    }

    public static void createRelationsInNodeRec(ModelPONode node) throws IllegalAccessException {

        for(Field field : getFieldsWithAccessible(node.modelPO.base.getClass(), RELATIVE_CLASSES)){
            if(field.getType().equals(List.class)){
                Class childClazz = getListGenericType(field);
                List<Base> childBases = new ArrayList<Base>();
                for(ModelPONode child : node.nodes){
                    if(child.modelPO.base.getClass().equals(childClazz)){
                        childBases.add(child.modelPO.base);
                    }
                }
                field.set(node.modelPO.base, childBases);
            } else {

                Class childClazz = field.getType();
                Base childBase = null;
                for(ModelPONode child : node.nodes){
                    if(child.modelPO.base.getClass().equals(childClazz)){
                        childBase = child.modelPO.base;
                        break;
                    }
                }
                if(childBase != null) {
                    field.set(node.modelPO.base, childBase);
                }
            }


        }

        for(ModelPONode child : node.nodes){
            child.modelPO.po.put(node.modelPO.po.getClassName(), node.modelPO.po);
        }

        for(ModelPONode child : node.nodes) {
            createRelationsInNodeRec(child);
        }
    }

    public static void createRelations(Map<Integer, List<ModelPONode>> map) throws IllegalAccessException {

        ModelPONode node = map.get(1).get(0);
        createRelationsInNodeRec(node);

    }

    public static void save(Map<Integer, List<ModelPONode>> map, boolean isCloudMode) throws ParseException {

        for(Integer key : new TreeSet<Integer>(map.keySet()).descendingSet()){
            for(Util.ModelPONode node : map.get(key)){
                for(Util.ModelPONode child : node.nodes){
                    if(isCloudMode){
                        child.modelPO.po.save();
                    } else {
                        child.modelPO.po.pin();
                    }
                }
            }
        }

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
