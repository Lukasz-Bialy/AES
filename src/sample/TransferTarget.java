package sample;

public class TransferTarget {
    private byte[] encodedSessionKey;
    private byte[] encodedAESParams;

    public TransferTarget(byte[] encodedSessionKey, byte[] encodedAESParams) {
        this.encodedSessionKey = encodedSessionKey;
        this.encodedAESParams = encodedAESParams;
    }
}
