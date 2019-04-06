package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class ServerController implements Control {

    private Stage stage;
    @FXML
    private Label pathLabel;

    private File file;

    private Window window;

    @FXML
    ListView clientList, receiversList;

    @FXML
    private ChoiceBox<String> modeChoiceBox;

    @FXML
    private ChoiceBox<String> subBlockChoiceBox;

    @FXML
    private void initialize() {
        this.window = new Window();
        initializeModeSelection();
        modeChoiceBox.setValue("ECB");
        modeChoiceBox.getItems().addAll("ECB", "CBC", "OFB", "CFB");
        subBlockChoiceBox.setValue("128");
        subBlockChoiceBox.getItems().addAll("8", "16", "32", "64", "128");
        clientList.getItems().addAll("Lukasz", "Profesor", "Bartosz");
    }


    @FXML
    void goBack() throws IOException {
        window.launchHome();
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
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open Resource File");
        file = filechooser.showOpenDialog(stage);

        if (file != null) {
            pathLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void encryptFile(ActionEvent event) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        if (file == null) {
            errorMessage("Problem with input file", "Choose file first");
        } else if (file.length() <= 104857600 && file.length() >= 1024) {
            String mode = modeChoiceBox.getValue();
            HashMap<String, Key> receivers = readPublicKeys(new ArrayList<String>(receiversList.getItems()));
            receivers.forEach((n, m) -> System.out.println(n + " " + m));
            if (receivers.isEmpty() == true) {
                errorMessage("No receivers", "Choose at least one receiver");
            } else {
                if (subBlockChoiceBox.isDisabled()) {
                    System.out.println(mode + " - Tryb szyfrowania");
                    Encryptor.encrypt(file, mode, receivers);
                } else {
                    mode = mode + subBlockChoiceBox.getValue();
                    System.out.println(mode + " - Tryb szyfrowania");
                    Encryptor.encrypt(file, mode, receivers);
                }
            }

        } else if (file.length() < 1024) {
            errorMessage("Input file too small", "Choose other file with size over 1kB");
        } else if (file.length() > 104857600) {
            errorMessage("Input file too large", "Choose other file with size less than 100MB");
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
            receivers.put(user, RSA.readPublicKey(user));
        }
        return receivers;
    }
}
