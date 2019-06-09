package sample;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static java.lang.System.out;

public class RSA {


    private static byte[] cryptography(Key key, byte[] resource, int mode) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(mode, key);
            return cipher.doFinal(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encryptHeader(Key key, byte[] resource) {
        Cipher cipher = null;
        int encLength = resource.length % 245 == 0 ? resource.length / 245 : resource.length / 245 + 1;
        try {
            byte[] result = new byte[encLength * 256];
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            int resourceIndex = 0;
            int resultIndex = 0;
            int bytesLeft = resource.length;
            while (bytesLeft > 245) {
                byte[] outputBuffer = cipher.doFinal(Arrays.copyOfRange(resource, resourceIndex, resourceIndex + 245));
                System.arraycopy(outputBuffer, 0, result, resultIndex, 256);
                resourceIndex = resourceIndex + 245;
                resultIndex = resultIndex + 256;
                bytesLeft = bytesLeft - resourceIndex;
            }
            byte[] outputbuffer = cipher.doFinal(Arrays.copyOfRange(resource, resourceIndex, resourceIndex + bytesLeft));
            System.arraycopy(outputbuffer, 0, result, resultIndex, 256);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptHeader(Key key, byte[] resource) {
        Cipher cipher = null;
        int decLength = resource.length / 256;
        try {
            byte[] result = new byte[decLength * 256];//???
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            int resourceIndex = 0;
            int resultIndex = 0;
            while (decLength > 0) {
                byte[] outputBuffer = cipher.doFinal(Arrays.copyOfRange(resource, resourceIndex, resourceIndex + 256));
                System.arraycopy(outputBuffer, 0, result, resultIndex, outputBuffer.length);
                resourceIndex = resourceIndex + 256;
                resultIndex = resultIndex + outputBuffer.length;
                decLength--;
            }
            byte[] finalResult = new byte[resultIndex];
            System.arraycopy(result, 0, finalResult, 0, resultIndex);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Collection<Byte>> encryptWithRSA(HashMap<String, Key> receivers, Header header) {
        Map<String, Collection<Byte>> encryptedUserKeys = new HashMap<>();//Task 14
        receivers.forEach((user, publicKey) -> {
            try {
                header.setUser(user);
                byte[] encodedHeaderArray = encryptHeader(publicKey, header.toBytes());
                Collection<Byte> encodedHeaderList = new ArrayList<Byte>();
                for (byte b : encodedHeaderArray
                ) {
                    encodedHeaderList.add(b);
                }
                encryptedUserKeys.put(user, encodedHeaderList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return encryptedUserKeys;
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        return kp;
    }

    public static PublicKey readPublicKey(String location) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = readFile(location);
        return bytesToPublicKey(bytes);
    }

    public static byte[] readFile(String location) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path path = Paths.get(location);
        byte[] bytes = Files.readAllBytes(path);
        return bytes;
    }

    public static PublicKey bytesToPublicKey(byte[] bytes) {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(ks);
            return publicKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey bytesToPrivateKey(byte[] bytes) throws BadPaddingException{
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(ks);
            out.println(privateKey);
            return privateKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SecretKeySpec sha256(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] messageDigest = md.digest(password.getBytes()); //Task 18
        SecretKeySpec keySpec = new SecretKeySpec(messageDigest, "AES");
        return keySpec;
    }

    public static void toFile(byte[] key, String destination) throws IOException {
        OutputStream out;
        out = new FileOutputStream(destination + ".key");
        out.write(key);
        out.close();
    }
}
