package com.aleksander.savosh.tasker.crypt;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Created by user on 9/10/15.
 */
public class AesCrypt2Impl {

    private Key key;
    private Cipher ecipher;
    private Cipher dcipher;

    public AesCrypt2Impl(String password) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException {
        this.key = createKey(password);
        this.ecipher = Cipher.getInstance("AES");
        this.dcipher = Cipher.getInstance("AES");
        this.ecipher.init(Cipher.ENCRYPT_MODE, key);
        this.dcipher.init(Cipher.DECRYPT_MODE, key);
    }


    public String encrypt(String plaintext) {
        try {
            return toHex(ecipher.doFinal(plaintext.getBytes("UTF8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String ciphertext) {
        try {
            return new String(dcipher.doFinal(toByte(ciphertext)), "UTF8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHex(byte [] buffer) {
        return new String(Hex.encodeHex(buffer));
    }

    public static byte[] toByte(String hex) throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
    }

    private static Key createKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
        byte[] key = password.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    public static void main(String[] args) throws InvalidKeySpecException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        System.out.println("-----------------------------------------------------");
        AesCrypt2Impl crypto = new AesCrypt2Impl("Password");
        String enc = crypto.encrypt("Andy");
        System.out.println("TEXT: " +  enc);

        AesCrypt2Impl anotherInst = new AesCrypt2Impl("Password");
        String anotherEncrypt = anotherInst.decrypt(enc);
        System.out.println("TEXT: " +  anotherEncrypt);
        System.out.println("-----------------------------------------------------");
    }
}
