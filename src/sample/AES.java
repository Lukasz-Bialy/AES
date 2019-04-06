package sample;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AES {

    private static SecretKey sessionKey;

    public static void generateSessionKey() {
        KeyGenerator keyGenerator = null;

        try {
            keyGenerator = KeyGenerator.getInstance("AES");//Generowanie Klucza sesyjnego
            keyGenerator.init(128);
            sessionKey = keyGenerator.generateKey();
            System.out.println(sessionKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static SecretKey getSessionKey() {
        return sessionKey;
    }

    public static Header encrypt(String mode, File inputFile, HashMap<String, Key> receivers, byte[] iv) {
        try {
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/" + mode + "/PKCS5Padding");
            if (mode.equals("ECB")) {
                iv = null;
                cipher.init(Cipher.ENCRYPT_MODE, sessionKey);
            } else {
                IvParameterSpec ivspec = new IvParameterSpec(iv);
                cipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivspec);

            }
            File outputFile = new File(inputFile + ".enc");
            processFile(cipher, inputFile, outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String extension = inputFile.getName().substring(inputFile.getName().lastIndexOf('.')+1);
        System.out.println(extension);
        Header encryptParams = new Header("AES", "128", "TODO", mode, extension);
        return encryptParams;
    }

    private static void processFile(Cipher cipher, File inputFile, File outputFile) throws java.io.IOException, IllegalBlockSizeException, BadPaddingException {
        FileOutputStream outputFileStream = new FileOutputStream(outputFile);
        FileInputStream inputFileStream = new FileInputStream(inputFile);
        byte[] inputBuffer = new byte[1024];
        int len;
        while ((len = inputFileStream.read(inputBuffer)) != -1) { //-1 means EOF
            byte[] outputBuffer = cipher.update(inputBuffer, 0, len);
            if (outputBuffer != null)
                outputFileStream.write(outputBuffer);
        }
        byte[] outputBuffer = cipher.doFinal();
        if (outputBuffer != null)
            outputFileStream.write(outputBuffer);
    }
}
