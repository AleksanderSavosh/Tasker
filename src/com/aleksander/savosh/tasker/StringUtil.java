package com.aleksander.savosh.tasker;


import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {


    public static String dateToReadableString(Date date){
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }




}
