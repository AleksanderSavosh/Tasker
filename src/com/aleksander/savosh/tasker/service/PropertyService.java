package com.aleksander.savosh.tasker.service;

import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.model.object.Notice;
import com.aleksander.savosh.tasker.model.object.Property;
import com.aleksander.savosh.tasker.model.object.PropertyType;

import java.util.*;

public class PropertyService {

    public static Map<Integer, List<Property>> getLocalNoticeProperties(Notice notice){
        List<Integer> propertyTypes = Arrays.asList(
                PropertyType.CREATE_DATE,
                PropertyType.TEXT,
                PropertyType.TITLE
        );
//        LocalDao<Property> propertyLocalDao = Application.getPropertyLocalDao();
        Map<Integer, List<Property>> map = new HashMap<Integer, List<Property>>();
//
//        for(Integer type : propertyTypes){
//            map.put(type, propertyLocalDao.read(Property.builder()
//                    .setType(type)
//                    .setNoticeId(notice.getObjectId())
//                    .build()));
//        }

        return map;
    }


    public static Map<Integer, List<Property>> convertToMap(List<Property> list){
        Map<Integer, List<Property>> map = new HashMap<Integer, List<Property>>();
        for(Property prop : list){
            if(map.containsKey(prop.getType())){
                map.get(prop.getType()).add(prop);
            } else {
                map.put(prop.getType(), new ArrayList<Property>(Arrays.asList(prop)));
            }
        }
        return map;
    }

    public static void validateProperties(List<Property> properties){
        // 1 title
        // 1 text
        int countTitles = 0;
        int countTexts = 0;
        int countCreateDate = 0;
        for(Property property : properties){
            switch (property.getType()){
                case PropertyType.TITLE: countTitles++; break;
                case PropertyType.TEXT: countTexts++; break;
                case PropertyType.CREATE_DATE: countCreateDate++; break;
            }
        }
        if(countTitles > 1){
            throw new RuntimeException("");
        }
        if(countTexts > 1){
            throw new RuntimeException("");
        }
        if(countCreateDate > 1){
            throw new RuntimeException("");
        }
    }


}
