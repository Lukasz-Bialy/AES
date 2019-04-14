package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Controller implements Control {



    @FXML
    private AnchorPane anchorp;

    private Stage stage;

    private Window window;

    public Controller() {
        window = new Window();
    }

    @FXML
    void selectFile(ActionEvent event) {
        FileChooser filec= new FileChooser();
        filec.setTitle("Open Resource File");
        filec.showOpenDialog(stage);
    }

    @FXML
    void launchClient() throws Exception{
        window.launchClient();
    }

    @FXML
    void launchServer() throws Exception {
        window.launchServer();
    }

    @FXML
    void generateKeyPair() {
        window.launchKeyGenerator();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    void testFunctionality() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        RSA.sha256("admin");
    }

}
