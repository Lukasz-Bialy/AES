package sample;

import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TCPClient extends Task<Boolean> {
    static Socket clientSocket;
    int port;
    static boolean listening;
    byte[] header;
    byte[] file;

    public TCPClient(int port) {
        this.port = port;
    }

    static void close() {
        try {
            clientSocket.close();
            listening = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Boolean call() throws Exception {
        clientSocket = new Socket("localhost", port);
        DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
        dOut.writeUTF("hello server");
        String s = dIn.readUTF();
        System.out.println(s);
        while (true) {
            listenForHeader(dIn);
        }
    }

    void listenForHeader(DataInputStream dIn) throws IOException {
        String s = dIn.readUTF();
        int length;
        if (s.equals("Header")) {
            System.out.println("nasluchiwanie headera");
            length = dIn.readInt();
            System.out.println(length);
            byte[] header = new byte[length];
            if (length > 0) {
                dIn.readFully(header);
            }
            this.header = header;
        }
        if (s.equals("File")){
            System.out.println("Waiting for file");
            length = (int)readFileLength(dIn);
            file = new byte[length];
            if(length > 0) {
                dIn.readFully(file);
            }
        }
    }

    private long readFileLength(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        byte[] fileLengthinBytes = new byte[length];
        if(length > 0) {
            dIn.readFully(fileLengthinBytes);
        }
        return bytesToLong(fileLengthinBytes);
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
