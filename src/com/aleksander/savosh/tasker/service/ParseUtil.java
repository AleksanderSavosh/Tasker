package com.aleksander.savosh.tasker.service;

import com.parse.ParseObject;

import java.util.Collection;
import java.util.Date;

public class ParseUtil {

    public static String toString(ParseObject parseObject){
        String objectId = parseObject.getObjectId() != null ?
                parseObject.getObjectId() : (String) parseObject.get("objectId");
        Date createdAt = parseObject.getCreatedAt() != null ?
                parseObject.getCreatedAt() : (Date) parseObject.get("createdAt");
        Date updatedAt = parseObject.getUpdatedAt() != null ?
                parseObject.getUpdatedAt() : (Date) parseObject.get("updatedId");;

        StringBuilder builder = new StringBuilder();
        builder.append(parseObject.getClassName() + "->{\n");
        builder.append("  objectId:" + objectId + "\n");
        builder.append("  createdAt:" + createdAt + "\n");
        builder.append("  updatedAt:" + createdAt + "\n");
        for(String key : parseObject.keySet()){
            Object value = parseObject.get(key);
            if(value instanceof Collection){
                builder.append("  " + key + ":[\n");
                for(ParseObject object : (Collection<ParseObject>) value){
                    builder.append("    ").append(toString(object)).append("\n");
                }
                builder.append("  ]");
                continue;
            }

            if(value instanceof ParseObject){
                builder.append("  " + key + ":{\n");
                builder.append("    " + toString((ParseObject) value)).append("\n");
                builder.append("  }");
                continue;
            }

            builder.append("  " + key + ":" + value + "\n");
        }
        builder.append("}");
        return builder.toString();
    }

}
