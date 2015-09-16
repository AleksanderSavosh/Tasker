package com.aleksander.savosh.tasker.util;


import java.lang.reflect.Field;
import java.util.*;

public class ReflectionUtil {

    public static Set<Field> getFields(Object src, Set<Class> types){
        Set<Field> all = new HashSet<Field>();
        Collections.addAll(all, src.getClass().getDeclaredFields());
        Collections.addAll(all, src.getClass().getFields());

        Iterator<Field> iter = all.iterator();
        while(iter.hasNext()){
            Field field = iter.next();
            field.setAccessible(true);
            Class type = field.getType();
            if(!types.contains(type)){
                iter.remove();
            }
        }
        return  all;
    }

}
