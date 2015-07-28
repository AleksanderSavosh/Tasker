package com.aleksander.savosh.tasker.service;

import com.aleksander.savosh.tasker.Application;
import com.aleksander.savosh.tasker.dao.LocalDao;
import com.aleksander.savosh.tasker.model.Notice;
import com.aleksander.savosh.tasker.model.Property;
import com.aleksander.savosh.tasker.model.PropertyType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyService {

    public static Map<Integer, List<Property>> getLocalNoticeProperties(Notice notice){
        List<Integer> propertyTypes = Arrays.asList(
                PropertyType.CREATE_DATE,
                PropertyType.TEXT,
                PropertyType.TITLE
        );
        LocalDao<Property> propertyLocalDao = Application.getPropertyLocalDao();
        Map<Integer, List<Property>> map = new HashMap<Integer, List<Property>>();

        for(Integer type : propertyTypes){
            map.put(type, propertyLocalDao.read(Property.builder()
                    .setType(type)
                    .setNoticeId(notice.getObjectId())
                    .build()));
        }

        return map;
    }





}
