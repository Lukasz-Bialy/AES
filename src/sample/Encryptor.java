package sample;


import javax.crypto.SecretKey;
import java.io.File;
import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Encryptor extends AES {

    private String mode;

    public void setMode(String mode) {
        this.mode = mode;
    }


    public String encrypt(File inputFile, HashMap<String, Key> receivers) {
        try {
            SecretKey sessionKey = AES.generateSessionKey();
            byte[] initializationVector = AES.randomInitVector();
            Header header = AES.encrypt(mode, inputFile, sessionKey, initializationVector);
            Map<String, Collection<Byte>> encryptedSessionKeys = RSA.encryptWithRSA(mode, receivers, sessionKey, header, initializationVector);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
}
