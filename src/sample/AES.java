package sample;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.KeySpec;
import java.util.*;

public class AES {

    public static byte[] randomInitVector() {
        byte[] initializationVector = new byte[16];
        SecureRandom srandom = new SecureRandom();
        srandom.nextBytes(initializationVector);
        return initializationVector;
    }

    public static SecretKey generateSessionKey() {
        KeyGenerator keyGenerator = null;
        SecretKey sessionKey = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");//Generowanie Klucza sesyjnego
            keyGenerator.init(128);
            sessionKey = keyGenerator.generateKey();
            System.out.println(sessionKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sessionKey;
    }

    public static byte[] encrypt(SecretKeySpec skspec, byte[] privateKey) {
        return cryptography(skspec, privateKey, Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt(SecretKeySpec skspec, byte[] privateKey) {
        return cryptography(skspec, privateKey, Cipher.DECRYPT_MODE);
    }

    private static byte[] cryptography(SecretKeySpec skspec, byte[] privateKey, int mode) {
        Cipher cipher = null;
        try {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(mode, skspec, ivspec);
            byte[] cryptoPrivate = cipher.doFinal(privateKey);
            return cryptoPrivate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Header encrypt(String mode, File inputFile, SecretKey sessionKey, byte[] iv) {
        IvParameterSpec ivspec = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/" + mode + "/PKCS5Padding");
            if (mode.equals("ECB")) {
                iv = null;
                cipher.init(Cipher.ENCRYPT_MODE, sessionKey);
            } else {
                ivspec = new IvParameterSpec(iv);
                cipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivspec);
            }
            File outputFile = new File(inputFile + ".enc");
            saveEncryptedFile(cipher, inputFile, outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String extension = inputFile.getName().substring(inputFile.getName().lastIndexOf('.') + 1);
        System.out.println(extension);
        Header encryptParams = new Header("AES", 128, 0, mode, extension, iv, sessionKey);
        return encryptParams;
    }

    private static void saveEncryptedFile(Cipher cipher, File inputFile, File outputFile) {
        FileOutputStream outputFileStream = null;
        try {
            outputFileStream = new FileOutputStream(outputFile);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
