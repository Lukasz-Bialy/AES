package sample;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.Key;
import java.util.HashMap;
import java.util.List;

public class Header implements Serializable {
    public String alghoritm;
    public int keySize;
    public int blockSize;
    public String mode;
    public String format;
    public byte[] initVector;
    public SecretKey secretKey;

    public Header(String alghoritm, int keySize, int blockSize, String mode, String format, byte[] initVector, SecretKey secretKey) {
        this.alghoritm = alghoritm;
        this.keySize = keySize;
        this.blockSize = blockSize;
        this.mode = mode;
        this.format = format;
        this.initVector = initVector;
        this.secretKey = secretKey;
    }

    public byte[] toBytes() {
        byte[] stream = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
            stream = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

    public static Header toHeader(byte[] stream) {
        Header header = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(stream);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            header = (Header) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return header;
    }

    @Override
    public String toString() {
        return "Header{" +
                "alghoritm='" + alghoritm + '\'' +
                ", keySize='" + keySize + '\'' +
                ", blockSize='" + blockSize + '\'' +
                ", mode='" + mode + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
