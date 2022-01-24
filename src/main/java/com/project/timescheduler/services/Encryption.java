package com.project.timescheduler.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Encryption {
    public String createHash(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512"); //using SHA-512 algorithm
            digest.update(input.getBytes());
            Formatter formatter = new Formatter();
            for(byte b: digest.digest()){
                formatter.format("%02x", b); //converting bytes in hex
            }
            return formatter.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
