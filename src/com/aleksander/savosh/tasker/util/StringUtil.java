package com.aleksander.savosh.tasker.util;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {

    public static boolean isEmpty(String s){
        return s == null || s.trim().length() == 0;
    }

    public static String dateToReadableString(Date date){
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    public static String encodePassword(String password){
        return new String(Hex.encodeHex(DigestUtils.md5(password)));
    }

}
