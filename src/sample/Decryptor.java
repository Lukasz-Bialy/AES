package sample;

import java.io.File;
import java.security.Key;

public class Decryptor extends AES {
    File folder;

    public Decryptor(String path) {
        folder = new File(path);
    }

    public boolean decrypt(String user, byte[] head, byte[] file, String password) {
        try {
            byte[] privateKeyBytes = RSA.readFile("PrivateKeys/Private" + user + ".key");
            System.out.println("Decrypting for user: " + user);
            byte[] decryptedPrivateKey = AES.decrypt(RSA.sha256(password), privateKeyBytes);
            Key privateKey = RSA.bytesToPrivateKey(decryptedPrivateKey);
            byte[] decryptedHeader = RSA.decryptHeader(privateKey, head);
            Header header = Header.toHeader(decryptedHeader);
            System.out.println(header.toString());
            AES.decrypt(header, file, folder);
            return true;
        } catch (Exception e) {
            System.out.println("Wrong password");
        }
        return false;
    }
}
