package sample;

import javafx.concurrent.Task;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class TCPServer extends Task<Boolean> {
    static ServerSocket serverSocket;
    int port;
    static boolean listening;
    Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private DataOutputStream dOut;
    private DataInputStream dIn;

    public TCPServer(int port) {
        this.port = port;
    }

    void initializeServerSocket() {
        try {
            serverSocket = new ServerSocket(this.port);//Task 2
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void close() {
        try {
            serverSocket.close();
            listening = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Boolean call() {
        System.out.println("Server listening on port: " + port);
        initializeServerSocket();
        listening = true;
        while (listening) {
            try {
                clientSocket = this.serverSocket.accept();
                dOut = new DataOutputStream(clientSocket.getOutputStream());
                dIn = new DataInputStream(clientSocket.getInputStream());
                System.out.println(clientSocket.getInetAddress());
                String greeting = dIn.readUTF();
                if ("hello server".equals(greeting)) {
                    dOut.writeUTF("hello client");
                } else {
                    out.println("unrecognised greeting");
                }
            } catch (Exception e) {
                System.out.println("Server socket closed");
            }
        }
        return false;
    }

    public void sendHeaders(Map<String, Collection<Byte>> receivers) throws IOException {//Task 3 Header
        dOut.writeUTF("Headers");
        dOut.writeInt(receivers.size());
        byte[] headerPrimitive = null;
        String user = null;
        for (Map.Entry<String, Collection<Byte>> entry : receivers.entrySet()) {
            user = entry.getKey();
            headerPrimitive = fillWithPrimitives(entry.getValue().toArray().length, entry.getValue());
            System.out.println("Wysylam header dla uzytkownika: " + user);
            sendOneHeader(user, headerPrimitive);
        }
    }

    public byte[] fillWithPrimitives(int length, Collection<Byte> encryptedHeader) {
        byte[] headerPrimitive = new byte[length];
        int i = 0;
        for (Byte b : encryptedHeader) {
            headerPrimitive[i] = b.byteValue();
            i++;
        }
        return headerPrimitive;
    }

    public void sendOneHeader(String user, byte[] primitiveHeader) throws IOException {
        dOut.writeUTF(user);
        dOut.writeInt(primitiveHeader.length);
        dOut.flush();
        dOut.write(primitiveHeader, 0, primitiveHeader.length);
        dOut.flush();
    }

    public void sendFile(String tempFileName) throws IOException {
        dOut.writeUTF("File");
        File file = new File("temporary.enc");
        byte[] fileLengthBytes = longToByte(file.length());

        dOut.writeInt(fileLengthBytes.length);
        dOut.write(fileLengthBytes);
        byte[] fileInBytes = new byte[(int) file.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        bis.read(fileInBytes, 0, fileInBytes.length);
        dOut.write(fileInBytes);
        file.delete();
    }

    private byte[] longToByte(long length) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(length);
        return buffer.array();

    }
}
