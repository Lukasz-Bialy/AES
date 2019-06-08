package sample;

import javax.crypto.SecretKey;
import java.io.*;

public class Header implements Serializable {
    public void setUser(String user) {
        this.user = user;
    }

    public String user;
    public String alghoritm;
    public int keySize;
    public int blockSize;
    public String mode;
    public String format;
    public byte[] initVector;
    public SecretKey sessionKey;

    public Header(String alghoritm, int keySize, int blockSize, String mode, String format, byte[] initVector, SecretKey sessionKey) {
        this.alghoritm = alghoritm;
        this.keySize = keySize;
        this.blockSize = blockSize;
        this.mode = mode;
        this.format = format;
        this.initVector = initVector;
        this.sessionKey = sessionKey;
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
                "user='" + user + '\'' +
                "alghoritm='" + alghoritm + '\'' +
                ", keySize='" + keySize + '\'' +
                ", blockSize='" + blockSize + '\'' +
                ", mode='" + mode + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
