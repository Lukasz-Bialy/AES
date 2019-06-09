package sample;


import javafx.scene.control.ProgressBar;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.Key;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Encryptor extends AES {

    private String mode;

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Header encrypt(File inputFile, ProgressBar progressBar) {
        try {
            SecretKey sessionKey = AES.generateSessionKey();
            progressBar.setProgress(0.1);
            byte[] initializationVector = AES.randomInitVector();
            progressBar.setProgress(0.2);
            return AES.encrypt(mode, inputFile, sessionKey, initializationVector, progressBar);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
}
