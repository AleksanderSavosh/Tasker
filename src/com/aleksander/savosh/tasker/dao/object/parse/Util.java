package com.aleksander.savosh.tasker.dao.object.parse;

import android.annotation.SuppressLint;
import android.util.Log;
import com.aleksander.savosh.tasker.dao.exception.DataNotFoundException;
import com.aleksander.savosh.tasker.dao.exception.OtherException;
import com.aleksander.savosh.tasker.dao.object.KeyValue;
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
            List<ParseObject> objects = ParseQuery.getQuery(clazz.getSimpleName())
                    .fromLocalDatastore().whereEqualTo("objectId", id).find();
            if(objects.size() == 1){
                return objects.get(0);
            } else if(objects.size() == 0){
                throw new DataNotFoundException();
            } else {
                throw new OtherException();
            }
        }
    }

    public static List<ParseObject> getPOs(Class clazz, boolean isCloudMode) throws ParseException,
            DataNotFoundException, OtherException {
        if(isCloudMode){
            return ParseQuery.getQuery(clazz.getSimpleName()).find();
        } else {
            return ParseQuery.getQuery(clazz.getSimpleName()).fromLocalDatastore().find();
        }
    }

    public static List<ParseObject> getPOsBy(Class clazz, boolean isCloudMode, KeyValue... keyValues) throws
            ParseException, DataNotFoundException, OtherException {
        ParseQuery query = ParseQuery.getQuery(clazz.getSimpleName());
        if(!isCloudMode){
            query.fromLocalDatastore();
        }
        for(int i = 0; i < keyValues.length; i++){
            query.whereEqualTo(keyValues[i].key, keyValues[i].value);
        }
        return query.find();
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

            Object value = field.get(base);
            if(value != null) {
                po.put(fieldName, value);
            }
        }
    }

    public static void setPO2Model(ParseObject po, Base base, boolean isCloudMode) throws IllegalAccessException {
        Class clazz = base.getClass();

        for(Field field : getFieldsWithAccessible(clazz, CLASSES)) {
            String fieldName = field.getName();
            Object value = null;
            if(fieldName.equals("objectId") && isCloudMode){
                value = po.getObjectId();
            }
            if(fieldName.equals("createdAt") && isCloudMode){
                value = po.getCreatedAt();
            }
            if(fieldName.equals("updatedAt") && isCloudMode){
                value = po.getUpdatedAt();
            }
            if(value == null) {
                value = po.get(fieldName);
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

    /**
     * Класс который описывает связку модели и парс объекта
     */
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

    /**
     * Класс который описывает узел который содержит в себе модель и ее узлы зависимостей
     */
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
            setPO2Model(po, node.modelPO.base, isCloudMode);
        }
        if(base != null && po == null){
            node.modelPO.po = isCreateMode || idIsEmpty(base.getObjectId()) ?
                    createPO(clazz) : getPO(clazz, base.getObjectId(), isCloudMode);
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

    /**
     * Создание дерева и карты уровней узлов. Развернутое представление зависимостей модели, под модели и т.д.
     * @param clazz
     * @param deep
     * @param base
     * @param po
     * @param isCreateMode
     * @param isCloudMode
     * @return
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ParseException
     * @throws OtherException
     * @throws DataNotFoundException
     */
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
                    List<Base> list = (List<Base>) field.get(base);
                    if(list != null && !list.isEmpty()){
                        for(Base childBase : list){
                            Map<Integer, List<ModelPONode>> childMap =
                                    getModelPoTreeRec(childClazz, deep + 1, childBase, null, isCreateMode, isCloudMode);

                            if(childMap.containsKey(deep + 1)){
                                node.nodes.addAll(childMap.get(deep + 1));
                            }
                            mergeMaps(map, childMap);
                        }
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

    public static void setPO2Model(Map<Integer, List<ModelPONode>> map, boolean isCloudMode) throws IllegalAccessException {
        for(Integer key : new TreeSet<Integer>(map.keySet()).descendingSet()){
            for(Util.ModelPONode node : map.get(key)){
                for(Util.ModelPONode child : node.nodes){
                    setPO2Model(child.modelPO.po, child.modelPO.base, isCloudMode);
                }
            }
        }
        setPO2Model(map.get(1).get(0).modelPO.po, map.get(1).get(0).modelPO.base, isCloudMode);
    }

    public static void save(Map<Integer, List<ModelPONode>> map, boolean isCloudMode) throws ParseException {
        for(Integer key : new TreeSet<Integer>(map.keySet()).descendingSet()){
            for(Util.ModelPONode node : map.get(key)){
                for(Util.ModelPONode child : node.nodes){
                    System.out.println(" save:" + child.modelPO.base);
                    if(isCloudMode){
                        child.modelPO.po.save();
                    } else {
                        child.modelPO.po.pin();
                        Log.i("BASE OBJECT PIN", "CLASS: " + child.modelPO.po.getClassName() + " ID: " + child.modelPO.po.get("objectId"));
                        Log.i("BASE OBJECT PIN", " BASE: " + child.modelPO.base);
                    }
                }
            }
        }
        System.out.println(" save:" + map.get(1).get(0).modelPO.base);
        if(isCloudMode){
            map.get(1).get(0).modelPO.po.save();
        } else {
            map.get(1).get(0).modelPO.po.pin();
            Log.i("BASE OBJECT PIN", "CLASS: " + map.get(1).get(0).modelPO.po.getClassName() + " ID: " + map.get(1).get(0).modelPO.po.get("objectId"));
            Log.i("BASE OBJECT PIN", " BASE: " + map.get(1).get(0).modelPO.base);
        }
    }

    public static void delete(Map<Integer, List<ModelPONode>> map, boolean isCloudMode) throws ParseException {
        for(Integer key : new TreeSet<Integer>(map.keySet()).descendingSet()){
            for(Util.ModelPONode node : map.get(key)){
                for(Util.ModelPONode child : node.nodes){
                    System.out.println(" delete:" + child.modelPO.base);
                    if(isCloudMode){
                        child.modelPO.po.delete();
                    } else {
                        child.modelPO.po.unpin();
                    }
                }
            }
        }
        System.out.println(" delete:" + map.get(1).get(0).modelPO.base);
        if(isCloudMode){
            map.get(1).get(0).modelPO.po.delete();
        } else {
            map.get(1).get(0).modelPO.po.unpin();
        }
    }

    public static void delete(List<ModelPO> modelPOs, boolean isCloudMode) throws ParseException {
        for(ModelPO modelPO : modelPOs){
            if(isCloudMode){
                modelPO.po.delete();
            } else {
                modelPO.po.unpin();
            }
        }
    }

    public static List<ModelPO> diff(Map<Integer, List<ModelPONode>> from, Map<Integer, List<ModelPONode>> what){
        Map<String, ModelPO> equals = new HashMap<String, ModelPO>();

        ModelPO rootFrom = from.get(1).get(0).modelPO;
        ModelPO rootWhat = what.get(1).get(0).modelPO;

        if(!idIsEmpty(rootFrom.base.getObjectId()) && !idIsEmpty(rootWhat.base.getObjectId()) &&
                rootFrom.base.getObjectId().equals(rootWhat.base.getObjectId())){
            equals.put(rootFrom.base.getObjectId(), rootFrom);
        }

        for(Integer key : new TreeSet<Integer>(from.keySet()).descendingSet()){
            for(Util.ModelPONode nodeFrom : from.get(key)){
                for(Util.ModelPONode childFrom : nodeFrom.nodes){

                    for(Util.ModelPONode nodeWhat : what.get(key)){
                        for(Util.ModelPONode childWhat : nodeWhat.nodes){

                            if(!idIsEmpty(childFrom.modelPO.base.getObjectId()) &&
                                    !idIsEmpty(childWhat.modelPO.base.getObjectId()) &&
                                    childFrom.modelPO.base.getObjectId().equals(childWhat.modelPO.base.getObjectId())){
                                equals.put(childFrom.modelPO.base.getObjectId(), childFrom.modelPO);
                            }

                        }
                    }

                }
            }
        }

        List<ModelPO> diff = new ArrayList<ModelPO>();

        for(Integer key : new TreeSet<Integer>(from.keySet()).descendingSet()){
            for(Util.ModelPONode nodeFrom : from.get(key)){
                for(Util.ModelPONode childFrom : nodeFrom.nodes){

                    if(!idIsEmpty(childFrom.modelPO.base.getObjectId())){
                        if(!equals.containsKey(childFrom.modelPO.base.getObjectId())){
                            diff.add(childFrom.modelPO);
                        }
                    } else {
                        diff.add(childFrom.modelPO);
                    }

                }
            }
        }

        return diff;
    }
}
