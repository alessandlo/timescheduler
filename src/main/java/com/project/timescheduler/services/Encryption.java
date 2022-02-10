package com.project.timescheduler.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * This class is responsible for the encryption of user data
 */
public class Encryption {
    /**
     * Hashes the entered password
     * @param input Password in plain text
     * @return Hashed Password
     */
    public String createHash(String input){
        String hashedOutput = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-512"); //using SHA3-512 algorithm
            digest.update(input.getBytes());
            Formatter formatter = new Formatter();
            for(byte b: digest.digest()){
                formatter.format("%02x", b); //converting bytes in hex
            }
            hashedOutput = formatter.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedOutput;
    }
}
