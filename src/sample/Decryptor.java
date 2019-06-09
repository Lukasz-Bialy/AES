package sample;

import javafx.scene.control.Alert;

import javax.crypto.BadPaddingException;
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
            byte[] decryptedPrivateKey = AES.decrypt(RSA.sha256(password), privateKeyBytes);//Task 19
            Key privateKey = RSA.bytesToPrivateKey(decryptedPrivateKey);
            byte[] decryptedHeader = RSA.decryptHeader(privateKey, head); //Task 9
            Header header = Header.toHeader(decryptedHeader);
            System.out.println(header.toString());
            AES.decrypt(header, file, folder);
            return true;
        } catch (BadPaddingException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Password");
            errorAlert.setContentText("Wrong user's password");//Task 16 and 17
            errorAlert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
