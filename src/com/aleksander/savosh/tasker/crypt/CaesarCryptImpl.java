package com.aleksander.savosh.tasker.crypt;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class CaesarCryptImpl implements Crypt {

    private enum Mode {
        EN, DE
    }

    @Override
    public String encrypt(String src, String password) throws CryptException {
        return doCrypt(src, password, Mode.EN);
    }

    @Override
    public String decrypt(String src, String password) throws CryptException {
        return doCrypt(src, password, Mode.DE);
    }

    private String doCrypt(String src, String password, Mode mode) throws CryptException {
        try {
            Integer key = password.hashCode();

            if(mode == Mode.DE){
                key = -key;
                src = new String(toByte(src));
            }

            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < src.length(); i++){
                char c = src.charAt(i);
                c += key ;
                builder.append(c);
            }

            if(mode == Mode.EN){
                src = toHex(builder.toString().getBytes());
            }

            return src;
        } catch (Exception e){
            throw new CryptException(e);
        }
    }

    public static String toHex(byte [] buffer) {
        return new String(Hex.encodeHex(buffer));
    }

    public static byte[] toByte(String hex) throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
    }

    public static void main(String[] args) throws CryptException {
        Crypt crypt = new CaesarCryptImpl();

        String src = "TEST TEST #1";
        src = crypt.encrypt(src, "Pass");
        System.out.println(src);
        System.out.println(crypt.decrypt(src, "Pass"));


    }

}
