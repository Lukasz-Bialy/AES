package sample.test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import sample.AES;
import sample.Decryptor;
import sample.Header;
import sample.RSA;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;


public class AESTest {
    KeyPair keyPair;
    byte[] beforeRead;
    byte[] afterRead;
    byte[] encAfter;
    {
        try {
            keyPair = RSA.generateKeyPair();
            beforeRead = sample.AES.encrypt(RSA.sha256("admin"), keyPair.getPrivate().getEncoded());
            RSA.toFile(beforeRead, "/home/lukasz/IdeaProjects/BSK/PrivateKeys/Privatetest");
            afterRead = RSA.readFile("PrivateKeys/Privatetest.key");
            encAfter = sample.AES.decrypt(RSA.sha256("admin"), afterRead);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readKeyFromFile() {
            assertArrayEquals(afterRead, beforeRead);
    }
    @Test
    public void comparePrivateKeys() {
            assertArrayEquals(encAfter, keyPair.getPrivate().getEncoded());
    }
    @Test
    public void compareKeys() {
            PrivateKey key = RSA.bytesToPrivateKey(encAfter);
            assertEquals(key, keyPair.getPrivate());
    }
    @Test
    public void differentPass() {
        PrivateKey key = RSA.bytesToPrivateKey(encAfter);
        assertEquals(key, keyPair.getPrivate());
    }
    @Test
    public void headerCryptography() {
        SecretKey key = AES.generateSessionKey();
        byte[] vector = AES.randomInitVector();
        Header header = new Header("AESTest", 128, 128, "ECB", "txt", vector, key);
        byte[] encryptedHeader = RSA.encryptHeader(keyPair.getPublic(), header.toBytes());
        byte[] decryptedHeader = RSA.decryptHeader(RSA.bytesToPrivateKey(encAfter), encryptedHeader);
        Header afterDec = Header.toHeader(decryptedHeader);
        assertArrayEquals(afterDec.toBytes(), header.toBytes());
    }

}