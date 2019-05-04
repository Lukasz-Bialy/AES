package sample;


import javax.crypto.SecretKey;
import java.io.File;
import java.net.ServerSocket;
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


    public Map<String, Collection<Byte>> encrypt(File inputFile, HashMap<String, Key> receivers) {
        try {
            SecretKey sessionKey = AES.generateSessionKey();
            byte[] initializationVector = AES.randomInitVector();
            Header header = AES.encrypt(mode, inputFile, sessionKey, initializationVector);
            Map<String, Collection<Byte>> encryptedSessionKeys = RSA.encryptWithRSA(receivers, header);
            return encryptedSessionKeys;
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
}
