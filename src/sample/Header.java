package sample;

import java.security.Key;
import java.util.HashMap;
import java.util.List;

public class Header {
    private String alghoritm;
    private String keySize;
    private String blockSize;
    private String mode;
    private String format;

    public Header(String alghoritm, String keySize, String blockSize, String mode, String format) {
        this.alghoritm = alghoritm;
        this.keySize = keySize;
        this.blockSize = blockSize;
        this.mode = mode;
        this.format = format;
    }
    public byte[] toBytes() {
        String message = alghoritm+';'+keySize+';'+blockSize+';'+mode+';'+format;
        return message.getBytes();
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
