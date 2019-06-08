package sample;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;

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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sessionKey;
    }

    public static byte[] encrypt(SecretKeySpec skspec, byte[] privateKey) throws Exception {
        return cryptography(skspec, privateKey, Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt(SecretKeySpec skspec, byte[] privateKey) throws Exception {
        return cryptography(skspec, privateKey, Cipher.DECRYPT_MODE);
    }

    private static byte[] cryptography(SecretKeySpec skspec, byte[] privateKey, int mode) throws Exception {
        Cipher cipher = null;
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, skspec, ivspec);
        byte[] cryptoPrivate = cipher.doFinal(privateKey);
        return cryptoPrivate;
    }

    public static void decrypt(Header header, byte[] inputFile, File output) {
        IvParameterSpec ivspec = null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/" + header.mode + "/PKCS5Padding");
            if (header.mode.equals("ECB")) {
                cipher.init(Cipher.DECRYPT_MODE, header.sessionKey);
            } else {
                ivspec = new IvParameterSpec(header.initVector);
                cipher.init(Cipher.DECRYPT_MODE, header.sessionKey, ivspec);
            }
            File outputFile = new File(output.getAbsolutePath() + "." + header.format);
            saveEncryptedFile(cipher, inputFile, outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Header encrypt(String mode, File inputFile, SecretKey sessionKey, byte[] iv, ProgressBar progressBar) {
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
            File outputFile = new File("temporary.enc");
            saveEncryptedFile(cipher, inputFile, outputFile, progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String extension = inputFile.getName().substring(inputFile.getName().lastIndexOf('.') + 1);
        System.out.println("Extension: " + extension);
        Header encryptParams = new Header("AES", 128, 0, mode, extension, iv, sessionKey);
        return encryptParams;
    }

    private static void saveEncryptedFile(Cipher cipher, byte[] inputFile, File outputFile) {
        FileOutputStream outputFileStream = null;
        try {
            outputFileStream = new FileOutputStream(outputFile);
            InputStream inputFileStream = new ByteArrayInputStream(inputFile);
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

    private static void saveEncryptedFile(Cipher cipher, File inputFile, File outputFile, ProgressBar progressBar) {
        new Thread() {
            public void run() {
                FileOutputStream outputFileStream = null;
                try {
                    outputFileStream = new FileOutputStream(outputFile);
                    FileInputStream inputFileStream = new FileInputStream(inputFile);
                    double singleProgress = 0.7 / (inputFile.length() / 1024);

                    double progress = 0.3;
                    byte[] inputBuffer = new byte[1024];
                    int len;
                    while ((len = inputFileStream.read(inputBuffer)) != -1) { //-1 means EOF
                        byte[] outputBuffer = cipher.update(inputBuffer, 0, len);
                        if (outputBuffer != null)
                            outputFileStream.write(outputBuffer);
                        progress += singleProgress;
                        final double prog = progress;
                        Platform.runLater(() -> progressBar.setProgress(prog));
                        // progressBar.setProgress(progress);
                    }
                    byte[] outputBuffer = cipher.doFinal();
                    if (outputBuffer != null)
                        outputFileStream.write(outputBuffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
