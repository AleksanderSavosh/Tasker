package com.aleksander.savosh.tasker.crypt;

public class CryptExx {

    public static void main(String[] args) throws Exception {
        String password = "password";
        String password2 = "9GSbslS2qJk7OIG";
        String text = "Test string Test string Test string Test string Test string ";


        AesCrypt eaes = new AesCrypt(password);
        String crypted = eaes.encrypt(text);
        System.out.println("crypted:" + crypted);


        AesCrypt daes = new AesCrypt(password);
        String decrypted = daes.decrypt(crypted);
        System.out.println("decrypted:" + decrypted);


        eaes = new AesCrypt(password2);
        crypted = eaes.encrypt(text);
        System.out.println("crypted:" + crypted);


        daes = new AesCrypt(password2);
        decrypted = daes.decrypt(crypted);
        System.out.println("decrypted:" + decrypted);

    }
}
