package com.aleksander.savosh.tasker;


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

//    public static String encodeText(String text, String secretCode){
//        return new String(Hex.encodeHex(DigestUtils.));
//    }


    public static class Caesar {
        private Reader in; // standard input stream for message
        private int key; // (en|de)cryption key

        // Caesar: constructor, opens standard input, passes key
        public Caesar(int k) {
            // open file
            in = new InputStreamReader(System.in);
            key = k;
        }
        // (en|de)crypt: just feed in opposite parameters
        public void encrypt() { translate(key); }
        public void decrypt() { translate(-key); }

        // translate: input message, translate
        private void translate(int k) {
            char c;
            while ((byte)(c = getNextChar()) != -1) {
                if (Character.isLowerCase(c)) {
                    c = rotate(c, k);
                }
                System.out.print(c);
            }
        }

        // getNextChar: fetches next char.
        public char getNextChar() {
            char ch = ' '; // = ' ' to keep compiler happy
            try {
                ch = (char)in.read();
            } catch (IOException e) {
                System.out.println("Exception reading character");
            }
            return ch;
        }
        // rotate: translate using rotation, version with table lookup
        public char rotate(char c, int key) { // c must be lowercase
            String s = "abcdefghijklmnopqrstuvwxyz";
            int i = 0;
            while (i < 26) {
                // extra +26 below because key might be negative
                if (c == s.charAt(i)) return s.charAt((i + key + 26)%26);
                i++;
            }
            return c;
        }


        // main: check command, (en|de)crypt, feed in key value
        public static void main(String[] args) {
            if (args.length != 2) {
                System.out.println("Usage: java Caesar (-d | -e) key");
                System.exit(1);
            }
            Caesar cipher = new Caesar(Integer.parseInt(args[1]));
            if (args[0].equals("-e")) cipher.encrypt();
            else if (args[0].equals("-d")) cipher.decrypt();
            else {
                System.out.println("Usage: java Caesar (-d | -e) key");
                System.exit(1);
            }
        }
    }

}
