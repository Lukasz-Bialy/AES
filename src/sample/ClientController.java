package sample;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ClientController implements Control {

    private Stage stage;
    @FXML
    private Label pathLabel;

    private Window window = new Window();

    @FXML
    void goBack() throws IOException {
        window.launchHome();
    }

    @FXML
    void decryptFIle(ActionEvent event){
        Decryptor.decrypt("howtodoinjava.com","ssshhhhhhhhhhh!!!!");
    }

    @FXML
    void chooseDsg(ActionEvent event) {
        DirectoryChooser dirchooser = new DirectoryChooser();
        dirchooser.setTitle("Open Resource File");
        File file = dirchooser.showDialog(stage);
        if(file != null){
            pathLabel.setText(file.getAbsolutePath());
        }
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
}
