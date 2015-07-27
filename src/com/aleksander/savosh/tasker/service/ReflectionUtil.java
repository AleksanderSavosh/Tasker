package com.aleksander.savosh.tasker.service;

import java.lang.reflect.Field;
import java.util.*;

public class ReflectionUtil {

    public static List<Field> getAllFields(Class objClass){
        Set<Field> fieldSet = new HashSet<Field>();
        Class temp = objClass;
        while(temp != null) {
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
}
