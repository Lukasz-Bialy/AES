package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController implements Control {

    private Stage stage;
    @FXML
    private Label pathLabel;

    private File file;

    private Encryptor encryptor;

    private Window window;

    TCPServer server;

    Map<String, Collection<Byte>> encryptedData;

    ExecutorService executorService;

    @FXML
    ProgressBar progressBar;

    @FXML
    ListView clientList, receiversList;

    @FXML
    private ChoiceBox<String> modeChoiceBox;

    @FXML
    private ChoiceBox<String> subBlockChoiceBox;

    @FXML
    private void initialize() throws Exception {
        server = new TCPServer(9000);
        Task nasluchiwanie = server;
        executorService = Executors.newFixedThreadPool(1);
        executorService.execute(nasluchiwanie);
        this.window = new Window();
        initializeModeSelection();
        modeChoiceBox.setValue("ECB");
        modeChoiceBox.getItems().addAll("ECB", "CBC", "OFB", "CFB"); //Task 10
        subBlockChoiceBox.setValue("128");
        subBlockChoiceBox.getItems().addAll("8", "16", "32", "64", "128");
        clientList.getItems().addAll(loadUsers());
        encryptor = new Encryptor();
    }

    @FXML
    void goBack() throws IOException {
        window.launchHome();
    }

    private Collection<String> loadUsers() {
        File folder = new File("PrivateKeys");
        File[] fileList = folder.listFiles();
        Collection<String> users = new ArrayList<String>();
        for (File f : fileList
        ) {
            String name = f.getName();
            if (name.contains(".key")) {
                name = name.split("Private")[1];
                name = name.split("\\.")[0];
                users.add(name);
            }
        }
        return users;
    }

    private void initializeModeSelection() {
        modeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue == (Number) 2 || newValue == (Number) 3) {
                    subBlockChoiceBox.setDisable(false);
                } else {
                    subBlockChoiceBox.setDisable(true);
                }
            }
        });
    }

    @FXML
    void chooseSrc(ActionEvent event) {
        FileChooser filechooser = new FileChooser();//Task 4
        filechooser.setTitle("Open Resource File");
        file = filechooser.showOpenDialog(stage);

        if (file != null) {
            pathLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void encryptFile(ActionEvent event) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        progressBar.setProgress(0);
        System.out.println("Start " + System.currentTimeMillis());
        String mode = modeChoiceBox.getValue();
        HashMap<String, Key> receivers = readPublicKeys(new ArrayList<String>(receiversList.getItems()));//Task 12
        if (validateInputFile() && validateFileSize() && !validateReceivers(receivers)) {
            if (!subBlockChoiceBox.isDisabled()) {
                mode = mode + subBlockChoiceBox.getValue();
            }
            System.out.println("Tryb szyfrowania: " + mode);
            encryptor.setMode(mode);
            long timeBefore = System.currentTimeMillis();
            encryptedData = encryptor.encrypt(file, receivers, progressBar);
            long timeAfter = System.currentTimeMillis();
            System.out.println("Time elapsed: " + (timeAfter-timeBefore));
        }
    }

    @FXML
    void sendPackage() {
        sendMessage();
    }

    private boolean validateInputFile() {
        if (file == null) {
            errorMessage("Problem with input file", "Choose file first");
            return false;
        }
        return true;
    }

    private boolean validateFileSize() {
        if (file.length() < 1024) {
            errorMessage("Input file too small", "Choose other file with size over 1kB");
            return false;
        } else if (file.length() > 104857600) { //Task 5
            errorMessage("Input file too large", "Choose other file with size less than 100MB");
            return false;
        }
        return true;
    }

    private boolean validateReceivers(HashMap<String, Key> receivers) {
        if (receivers.isEmpty()) {
            errorMessage("No receivers", "Choose at least one receiver");
            return true;
        }
        return false;
    }

    private void sendMessage() {
        try {
            System.out.println("Sending start " + System.currentTimeMillis());
           // Map<String, Collection<Byte>> encryptedSessionKeys = RSA.encryptWithRSA(receivers, header);//Task 12
            server.sendHeaders(encryptedData); //Task 3
            server.sendFile("temporary.enc"); //Task 3
        } catch (Exception e) {
            errorMessage("Blad mapy zaszyfrowanych uzytkownikow", "Brak danych o zaszyfrowanych uzytkownikach");
        }
    }

    private void errorMessage(String header, String text) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(text);
        errorAlert.showAndWait();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void selectClient(ActionEvent actionEvent) {
        transferClients(receiversList, clientList);
    }

    public void unselectClient(ActionEvent actionEvent) {
        transferClients(clientList, receiversList);
    }

    @FXML
    void exitWindow() {
        TCPServer.close();
        executorService.shutdown();
        stage.close();
    }

    private void transferClients(ListView target, ListView source) {
        ObservableList<String> selectedList = source.getSelectionModel().getSelectedItems();
        if (selectedList != null) {
            target.getItems().addAll(selectedList);
            source.getItems().removeAll(selectedList);
        }
    }

    private HashMap<String, Key> readPublicKeys(ArrayList<String> selectedClients) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        HashMap<String, Key> receivers = new HashMap<>();
        for (String user : selectedClients
        ) {
            receivers.put(user, RSA.readPublicKey("PublicKeys/Public" + user + ".key"));
        }
        return receivers;
    }

}
