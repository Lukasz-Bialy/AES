package sample;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sun.security.krb5.EncryptedData;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientController implements Control {

    private Socket clientSocket;

    private Stage stage;

    @FXML
    Label fileReceivedStatus;

    @FXML
    private Label pathLabel;

    ExecutorService executorService;

    @FXML
    TextField passwordField;

    File folder;

    @FXML
    Label statusLabel;

    @FXML
    ListView receiversList;

    ArrayList<String> users;

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
    private void initialize() throws Exception {
        this.window = new Window();
        users = new ArrayList<>();
        loadUsers("/home/lukasz/IdeaProjects/BSK/users.txt");
        receiversList.getItems().addAll(users);
    }

    private void loadUsers(String path) throws Exception {
        File file = new File(path);
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            users.add(line);
        }
    }

    @FXML
    void decryptFile(ActionEvent event) {
        String user = selectUser();
        String password = passwordField.getText();
        if (validateFolder() && validateUser(user) && validatePassword(password)) {
            decryptor = new Decryptor(folder.getAbsolutePath() + "/" + filename.getText()); //Task 7
            System.out.println(folder.getAbsolutePath() + "/" + filename.getText());
            long timeBefore = System.currentTimeMillis();
            decryptor.decrypt(user, client.receivers.get(user).header, client.file, password);
            long timeAfter = System.currentTimeMillis();
            System.out.println("Decrypted in: " + (timeAfter-timeBefore) +"ms");
        } else {
            errorMessage("Authentication failed", "Wrong username or password");
        }
    }

    boolean validateFolder() {
        if (folder == null) {
            errorMessage("Empty localization", "Choose localization");
            return false;
        }
        return true;
    }

    boolean validateUser(String user) {
        System.out.println(user);
        if (user == null) {
            errorMessage("User", "Choose user from the list");
            return false;
        } else if (!client.receivers.containsKey(user)) {
            errorMessage(user, "Ten użytkownik nie jest upoważniony do pliku");
            return false;
        }
        return true;
    }

    boolean validatePassword(String password) {
        if (password.isEmpty()) {
            errorMessage("Password", "Insert password");
            return false;
        }
        return true;
    }

    private String selectUser() {
        ArrayList<String> list = new ArrayList<>(receiversList.getSelectionModel().getSelectedItems());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @FXML
    void chooseDsg(ActionEvent event) {
        DirectoryChooser dirchooser = new DirectoryChooser();
        dirchooser.setTitle("Open Resource File");
        folder = dirchooser.showDialog(stage);
        if (folder != null) {
            pathLabel.setText(folder.getAbsolutePath());
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void errorMessage(String header, String text) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(text);
        errorAlert.showAndWait();
    }

    public void exitWindow(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void establishConnection(ActionEvent actionEvent) {
        client = new TCPClient(9000, statusLabel, fileReceivedStatus);
        Task nasluchiwanie = client;
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(nasluchiwanie);
    }
}
