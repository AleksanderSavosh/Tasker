package com.aleksander.savosh.tasker.crypt;

import android.util.Base64;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
//import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AesCrypt {

    byte [] seed;

    public AesCrypt(String password) {
        seed = password.getBytes();
    }

    public AesCrypt(byte[] password) {
        seed = password;
    }

    public String encrypt(String cleartext) throws AesException {
        try {
            byte[] rawKey = getRawKey(seed);
            byte[] result = encrypt(rawKey, cleartext.getBytes());
            return toHex(result);
        } catch (NoSuchAlgorithmException e) {
            throw new AesException("AES encrypt error", e);
        } catch (NoSuchPaddingException e) {
            throw new AesException("AES encrypt error", e);
        } catch (InvalidKeyException e) {
            throw new AesException("AES encrypt error", e);
        } catch (IllegalBlockSizeException e) {
            throw new AesException("AES encrypt error", e);
        } catch (BadPaddingException e) {
            throw new AesException("AES encrypt error", e);
        }
    }

    public String decrypt(String encrypted) throws AesException {
        try {
            byte[] rawKey = getRawKey(seed);
            byte[] enc = toByte(encrypted);
            byte[] result = decrypt(rawKey, enc);
            return new String(result);
        } catch (NoSuchAlgorithmException e) {
            throw new AesException("AES decrypt error", e);
        } catch (NoSuchPaddingException e) {
            throw new AesException("AES decrypt error", e);
        } catch (InvalidKeyException e) {
            throw new AesException("AES decrypt error", e);
        } catch (IllegalBlockSizeException e) {
            throw new AesException("AES decrypt error", e);
        } catch (BadPaddingException e) {
            throw new AesException("AES decrypt error", e);
        } catch (DecoderException e) {
            throw new AesException("AES decrypt error", e);
        }
    }

    private static byte[] getRawKey(byte[] password) throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(password);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(byte [] buffer) {
        return new String(Hex.encodeHex(buffer));
//        return Base64.encodeToString(buffer, Base64.DEFAULT);
//        return DatatypeConverter.printBase64Binary(buffer);
    }

    public static byte[] toByte(String hex) throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
//        return Base64.decode(hex, Base64.DEFAULT);
//        return DatatypeConverter.parseBase64Binary(hex);
    }



}
