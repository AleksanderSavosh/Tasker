package com.aleksander.savosh.tasker.crypt;


import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesCryptImpl implements Crypt {

    private String byteToString(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        builder.append("(" + bytes.length + " byte) ");
        builder.append("(" + bytes.length * 8 + " bit) ");
        for(byte aByte : bytes){
            builder.append(aByte).append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private byte[] resizeTo(byte[] bytes, int bitCount){
        int byteCount = bitCount/8;
        byte[] result = new byte[byteCount];
        for(int i = 0; i < result.length; i++){
            result[i] = i < bytes.length ? bytes[i] : 113;
        }
        return result;
    }

    @Override
    public String encrypt(String src, String password) throws CryptException {
        System.out.println("--- === ENCODE MODE === ---");
        try {

            System.out.println("     src: " + src);
            System.out.println("password: " + password);

            byte[] srcBytes = src.getBytes();
            byte[] passBytes = password.getBytes();
            byte[] resizedPassBytes = resizeTo(passBytes, 128);

            System.out.println("        srcBytes: " + byteToString(srcBytes));
            System.out.println("       passBytes: " + byteToString(passBytes));
            System.out.println("resizedPassBytes: " + byteToString(resizedPassBytes));

            SecretKeySpec skeySpec = new SecretKeySpec(resizedPassBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            byte[] resultBytes = cipher.doFinal(srcBytes);
            System.out.println("resultBytes: " + byteToString(resultBytes));

            System.out.println("convert to Base64");

            String result = Base64.encodeToString(resultBytes, Base64.DEFAULT);
            System.out.println("result: " + result);

            return result;
        } catch (Exception e){
            throw new CryptException(e);
        } finally {
            System.out.println("--- === ENCODE END === ---");
        }
    }

    @Override
    public String decrypt(String src, String password) throws CryptException {
        System.out.println("--- === DECODE MODE === ---");
        try {

            System.out.println("     src: " + src);
            System.out.println("password: " + password);

            System.out.println("convert from Base64");

            byte[] srcBytes = Base64.decode(src, Base64.DEFAULT);
            byte[] passBytes = password.getBytes();
            byte[] resizedPassBytes = resizeTo(passBytes, 128);

            System.out.println("        srcBytes: " + byteToString(srcBytes));
            System.out.println("       passBytes: " + byteToString(passBytes));
            System.out.println("resizedPassBytes: " + byteToString(resizedPassBytes));


            SecretKeySpec skeySpec = new SecretKeySpec(resizedPassBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] resultBytes = cipher.doFinal(srcBytes);
            System.out.println("resultBytes: " + byteToString(resultBytes));

            String result = new String(resultBytes);
            System.out.println("result: " + result);

            return result;
        } catch (Exception e){
            throw new CryptException(e);
        } finally {
            System.out.println("--- === ENCODE END === ---");
        }
    }
}
