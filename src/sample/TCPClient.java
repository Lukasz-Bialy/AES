package sample;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.IntStream;

public class TCPClient extends Task<Boolean> {
    static Socket clientSocket;
    int port;
    Map<String, ByteHeader> receivers = new HashMap<>();
    byte[] header;
    byte[] file;
    Label statusLabel;
    Label fileReceivedStatus;

    public TCPClient(int port, Label statusLabel, Label fileReceivedStatus) {
        this.fileReceivedStatus = fileReceivedStatus;
        this.statusLabel = statusLabel;
        this.port = port;
    }

    static void close() {
        try {
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Boolean call() throws Exception {
        clientSocket = new Socket("localhost", port); //Task 2
        DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
        dOut.writeUTF("hello server");
        statusLabel.setTextFill(Color.web("#00e64d",1));
        String s = dIn.readUTF();
        System.out.println(s);
        while (true) {
            listenForHeader(dIn);
        }
    }

    void listenForHeader(DataInputStream dIn) throws IOException {
        String s = dIn.readUTF();
        int length;
        if (s.equals("Headers")) {
            int numberOfUsers = dIn.readInt();
            System.out.println("Number of users: " + numberOfUsers);
            System.out.println();
            for (int i = 0; i < numberOfUsers; i++) {
                String user = dIn.readUTF();
                length = dIn.readInt();
                ByteHeader headerWrapper = new ByteHeader(length);
                if (length > 0) {
                    dIn.readFully(headerWrapper.header);
                }
                receivers.put(user, headerWrapper);
            }
        }
        receivers.forEach((x,y)-> System.out.println(x+": "+y));
        if (s.equals("File")) {
            System.out.println("Waiting for file");
            length = (int) readFileLength(dIn);
            file = new byte[length];
            if (length > 0) {
                dIn.readFully(file);
            }
            System.out.println("Sending finish " + System.currentTimeMillis());
            fileReceivedStatus.setTextFill(Color.web("#00e64d",1));
        }
    }

    public Collection<Byte> bytesToBytes(byte[] header) {
        Collection<Byte> headerInBytes = new ArrayList<>();
        for (int i = 0; i < header.length; i++) {
            headerInBytes.add(new Byte(header[i]));
        }
        return headerInBytes;
    }

    private long readFileLength(DataInputStream dIn) throws IOException {
        int length = dIn.readInt();
        byte[] fileLengthinBytes = new byte[length];
        if (length > 0) {
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
