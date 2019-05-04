package sample;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Decryptor extends AES {
    File folder;

    public Decryptor(String path) {
        folder = new File(path);
    }

    public void decrypt(String user, byte[] head, byte[] file) {
        //byte[] head = {-86, -67, 57, -83, -128, 88, 81, -56, -107, -55, 36, -19, 54, 127, -85, -84, 26, -65, -26, -14, -127, -44, 33, 127, -108, -93, 49, 75, -16, -31, 48, -113, -80, 108, -28, -45, -116, 23, -38, -16, -51, -76, 33, 30, -72, -54, 79, 53, 100, 19, 59, -12, 43, 42, 78, 60, -45, -57, 73, 16, -92, 57, 122, -67, 93, -125, -92, -113, -67, -21, 22, 96, -112, 93, 12, -122, 46, 32, -16, 5, 46, 63, -58, 16, 5, -60, 37, -16, 22, -67, 102, 22, 41, 10, -93, -90, -39, 0, 38, -7, 59, -111, -65, -20, -30, -72, 122, 89, 82, 8, -8, -14, 13, -79, 51, -45, -125, 35, -87, -38, -77, -26, 68, 101, 104, -105, 4, 22, -42, -38, -63, 48, -12, -21, -51, 110, 57, -48, -53, 103, -32, 21, 50, 51, 95, -81, 66, -73, -36, 36, 91, -95, 58, 44, 33, 40, -99, 118, -45, -28, 20, 14, -104, -38, -126, 65, -23, -110, -53, -123, -108, -83, -100, -110, -113, -15, -24, -35, 70, -116, -52, 112, -53, -62, 1, -7, 76, 98, 53, -95, -23, 36, 102, 69, -75, -125, -125, 1, 124, 25, 55, -77, -14, -10, -126, -98, -120, 21, -18, 81, 13, 64, 126, -110, -99, -122, -58, 112, 73, 126, 62, -14, 93, -111, -4, -100, -97, -29, -53, -79, -21, 23, 111, 42, -124, -83, -107, 47, -58, 18, -108, -53, 23, 25, 35, 34, -41, 70, 66, -90, 6, -112, -98, 124, -50, 36, 107, -106, -87, -103, 19, 23, -29, -103, 40, -76, -63, -93, -60, 36, -33, -3, -50, -80, -10, 59, 123, -71, -5, -10, 28, 102, 114, 23, 107, -80, -105, -26, -66, 42, 70, -124, -105, -28, 123, -112, -54, -52, -87, -40, 9, -92, -117, 29, -98, 20, -70, -56, -31, 88, -44, -73, -47, -60, -64, 24, -87, 56, 99, -8, -127, 77, 16, 26, 21, -43, -38, -71, 121, -13, -55, 13, 97, -120, 19, -68, 28, 81, 10, -90, 44, -111, -35, 53, 24, -89, -15, -75, 86, -63, 29, -116, 124, -43, -27, -97, -6, -78, -27, -59, 47, -122, 106, -60, 26, -38, 63, 124, -16, 24, 92, -109, 89, -84, 71, 41, -112, -43, -117, -40, 87, -41, 92, -73, -59, -72, 76, -95, 101, 97, 98, 14, 100, 8, -32, 60, -121, -80, -72, -112, 84, -119, 94, -45, -101, 81, 72, 70, 31, -31, -87, 90, 62, -54, -122, 116, 44, 12, -9, -96, 43, -43, -94, 127, -20, -127, -111, -86, -120, -28, -127, -110, -66, 67, -15, -92, 51, 23, -105, 82, -87, 124, 72, 122, 39, -14, -87, 10, -37, 29, -87, 37, 52, -50, -111, -56, 51, 57, 26, 38, -29, 50, -35, -75, -70, -61, -53, 62, -75, 79, -12, 123, -46, 88, -128, -106, 6, 91, 4, 42, -99, -35, 116, -120, 35, 47, 63, 39, 31, -65, 40, 85, 40, -83, 43, 63, 42, 76, -118, -9, 97, -86, 4, 108, 101, -46, 10, 101, 75, -46, 18, 118};
        String location;
        try {
            byte[] privateKeyBytes = RSA.readFile("PrivateKeys/Private"+user+".key");
            byte[] decryptedPrivateKey = AES.decrypt(RSA.sha256("admin"),privateKeyBytes);
            Key privateKey = RSA.bytesToPrivateKey(decryptedPrivateKey);
            byte[] decryptedHeader = RSA.decryptHeader(privateKey, head);
            Header header = Header.toHeader(decryptedHeader);
            System.out.println(header.toString());
            //AES.decrypt(header, folder);
            AES.decrypt(header, file, folder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
