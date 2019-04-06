package sample;

import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

public class RSA {
    public static byte[] encryptWithRSA(String mode, Key publicKey, byte[] resource) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(resource);
    }

    public static Map<String, TransferTarget> encryptWithRSA(String mode, HashMap<String, Key> receivers, SecretKey sessionKey, Header encryptionParams) {
        Map<String, TransferTarget> encryptedUserKeys = new HashMap<>();
        receivers.forEach((user, publicKey) -> {
            try {
                TransferTarget tt = new TransferTarget(encryptWithRSA(mode, publicKey, sessionKey.getEncoded()), encryptWithRSA(mode, publicKey, encryptionParams.toBytes()));
                encryptedUserKeys.put(user, tt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return encryptedUserKeys;
    }

    public static void decryptWithRSA() {

    }

    public static void generateKeyPair(String user) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        Key publicKey = kp.getPublic();
        Key privateKey = kp.getPrivate();
        out.println(publicKey);
        toFile(publicKey, user, "Public");
        toFile(privateKey, user, "Private");
    }

    public static void toFile(Key key, String name, String type) throws IOException {
        OutputStream out;
        String outFile = name;
        out = new FileOutputStream(type + outFile + ".key");
        out.write(key.getEncoded());
        out.close();
    }

    public static Key readPublicKey(String user) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path path = Paths.get("PublicKeys/Public" + user + ".key");
        byte[] bytes = Files.readAllBytes(path);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(ks);
        out.println(publicKey);
        return publicKey;
    }

}
