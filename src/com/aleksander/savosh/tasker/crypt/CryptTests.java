package com.aleksander.savosh.tasker.crypt;

public class CryptTests {

    static Crypt crypt = new AesCryptImpl();

    public static void main(String[] args) throws CryptException {
        String password = "Password";
        String text = "Some text";

        String encodeText = crypt.encrypt(text, password);
        String decodeText = crypt.decrypt(text, password);

        System.out.println(" --- === RESULTS === --- ");
        System.out.println("      text: " + text);
        System.out.println("  password: " + password);
        System.out.println("encodeText: " + encodeText);
        System.out.println("decodeText: " + decodeText);
    }



}
