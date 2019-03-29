package sample;

import sun.security.provider.SecureRandom;
/*https://security.stackexchange.com/questions/47871/how-securely-random-is-oracles-java-security-securerandom*/

public class Generator {
    private SecureRandom pseudoNumber;
    private static final Generator instance = new Generator();

    private Generator() {
        this.pseudoNumber = new SecureRandom();
    }
    public byte[] generateSesionKey(){
        byte bytes[] = new byte[16];
        pseudoNumber.engineNextBytes(bytes);
        return bytes;
    }
    public static Generator getInstance() {
        return instance;
    }

}
