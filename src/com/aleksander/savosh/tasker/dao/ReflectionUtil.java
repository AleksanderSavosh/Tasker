package com.aleksander.savosh.tasker.dao;

import com.aleksander.savosh.tasker.model.object.Account;
import com.aleksander.savosh.tasker.model.object.Base;

import java.lang.reflect.Field;
import java.util.*;

public class ReflectionUtil {

    public static List<Field> getAllFieldsWithoutObjects(Class objClass){
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

    public static List<Field> getAllFieldsObjects(Class objClass){
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

    public static List<Field> getAllFields(Class objClass){
        Set<Field> fieldSet = new HashSet<Field>();
        Class temp = objClass;
        while(temp != null && temp != Object.class) {
            fieldSet.addAll(Arrays.asList(temp.getFields()));
            fieldSet.addAll(Arrays.asList(temp.getDeclaredFields()));
            temp = temp.getSuperclass();
        }
        return new ArrayList<Field>(fieldSet);
    }

    public static Map<String, Field> fieldsListToMap(List<Field> fields){
        Map<String, Field> map = new HashMap<String, Field>();
        for(Field field : fields){
            map.put(field.getName(), field);
        }
        return map;
    }

    public static void main(String[] args) {

        System.out.println(getAllFieldsWithoutObjects(Account.class));
        System.out.println(getAllFieldsObjects(Account.class));


    }
}
