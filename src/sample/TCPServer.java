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
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void close(){
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
        while(listening) {
            try {
                clientSocket = this.serverSocket.accept();
                dOut = new DataOutputStream(clientSocket.getOutputStream());
                dIn = new DataInputStream(clientSocket.getInputStream());
                System.out.println(clientSocket.getInetAddress());
                String greeting = dIn.readUTF();
                if ("hello server".equals(greeting)) {
                    dOut.writeUTF("hello client");
                }
                else {
                    out.println("unrecognised greeting");
                }
            } catch (Exception e) {
                System.out.println("Server socket closed");
            }
        }
        return false;
    }

    public void sendHeader(Map<String, Collection<Byte>> receivers) throws IOException {
        dOut.writeUTF("Header");
        byte[] headerPrimitive = new byte[receivers.get("Tester2").toArray().length];
        System.out.println();
        int i = 0;
        for (Byte b:receivers.get("Tester2")
             ) {
            headerPrimitive[i] = b.byteValue();
            System.out.print(b.byteValue()+" ");
            i++;
        }
        dOut.writeInt(headerPrimitive.length);
        dOut.flush();
        dOut.write(headerPrimitive, 0, headerPrimitive.length);
        dOut.flush();
    }

    public void sendFile(String tempFileName) throws IOException {
        dOut.writeUTF("File");
        File file = new File("temporary.enc");
        byte[] fileLengthBytes = longToByte(file.length());

        dOut.writeInt(fileLengthBytes.length);
        dOut.write(fileLengthBytes);
        byte[] fileInBytes = new byte[(int)file.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        bis.read(fileInBytes, 0, fileInBytes.length);
        dOut.write(fileInBytes);
    }

    private byte[] longToByte(long length){
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(length);
        return buffer.array();

    }
}
