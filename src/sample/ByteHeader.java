package sample;

public class ByteHeader {
    public byte[] header;

    public ByteHeader(int length) {
        this.header = new byte[length];
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }
}
