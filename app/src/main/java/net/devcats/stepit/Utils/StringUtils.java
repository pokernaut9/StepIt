package net.devcats.stepit.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

    public static String encrypt(String message) {

        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(message.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte byteIndex : messageDigest) {
                hexString.append(Integer.toHexString(0xFF & byteIndex));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
