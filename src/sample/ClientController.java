package sample;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sun.security.krb5.EncryptedData;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController implements Control {

    private Socket clientSocket;
    private Stage stage;
    @FXML
    private Label pathLabel;

    File folder;
    @FXML
    ListView receiversList;

    @FXML
    TextField filename;

    private Decryptor decryptor;
    private Window window = new Window();

    TCPClient client;

    @FXML
    void goBack() throws IOException {
        window.launchHome();
    }

    @FXML
    private void initialize() {
        client = new TCPClient(9000);
        Task nasluchiwanie = client;
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(nasluchiwanie);
        this.window = new Window();
        receiversList.getItems().addAll(loadUsers());
    }
    @FXML
    boolean checkConnection() {
        try {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer.writeBytes("Hello Server");
            inFromServer.readLine();
            System.out.println(inFromServer);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Collection<String> loadUsers(){
        File folder = new File("PublicKeys");
        File[] fileList = folder.listFiles();
        Collection<String> users = new ArrayList<String>();
        for (File f:fileList
        ) {
            String name = f.getName();
            if(name.contains(".key")){
                name = name.split("Public")[1];
                name = name.split("\\.")[0];
                users.add(name);
            }
        }
        return users;
    }

    @FXML
    void decryptFile(ActionEvent event){
        decryptor = new Decryptor(folder.getAbsolutePath()+"/"+filename.getText());
        System.out.println(folder.getAbsolutePath()+"/"+filename.getText());
        decryptor.decrypt("Tester2", client.header, client.file);
    }

    @FXML
    void testAction() {

    }

    @FXML
    void chooseDsg(ActionEvent event) {
        DirectoryChooser dirchooser = new DirectoryChooser();
        dirchooser.setTitle("Open Resource File");
        folder = dirchooser.showDialog(stage);
        if(folder != null){
            pathLabel.setText(folder.getAbsolutePath());
        }
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
}
