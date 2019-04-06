package sample;


import java.io.File;
import java.security.Key;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Encryptor extends AES {
    private static byte[] initializationVector;
    private static Header header;

    private static void randomInitVector() {
        initializationVector = new byte[16];
        SecureRandom srandom = new SecureRandom();
        srandom.nextBytes(initializationVector);
    }

    public static String encrypt(File inputFile, String mode, HashMap<String, Key> receivers) {
        try {
            AES.generateSessionKey();
            randomInitVector();
            header = AES.encrypt(mode, inputFile, receivers, initializationVector);
            Map<String, TransferTarget> encryptedSessionKeys = RSA.encryptWithRSA(mode, receivers, AES.getSessionKey(), header);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }


}
