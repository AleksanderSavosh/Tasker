package com.aleksander.savosh.tasker.crypt;

public interface Crypt {

    public String encrypt(String src, String password) throws CryptException;

    public String decrypt(String src, String password) throws CryptException;

}
