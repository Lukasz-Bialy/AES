package sample;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ServerController implements Control {

    private Stage stage;
    @FXML
    private Label pathLabel;

    private File file;

    Generator gienerator;

    private Window window;

    @FXML
    private ChoiceBox<String> modeChoiceBox;

    @FXML
    private ChoiceBox<String> subBlockChoiceBox;

    @FXML
    private void initialize(){
        this.window = new Window();
        gienerator = Generator.getInstance();
        modeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue == (Number)2 || newValue == (Number)3) {
                    subBlockChoiceBox.setDisable(false);
                }
                else{
                    subBlockChoiceBox.setDisable(true);
                }
            }
        });
        modeChoiceBox.setValue("ECB");
        modeChoiceBox.getItems().addAll("ECB", "CBC", "OFB", "CFB");
        subBlockChoiceBox.setValue("128");
        subBlockChoiceBox.getItems().addAll("2", "4", "8", "16", "32", "64", "128");
    }

    @FXML
    void disableSubBlock(){

    }

    @FXML
    void goBack() throws IOException {
        window.launchHome();
    }

    @FXML
    void chooseSrc(ActionEvent event) {
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open Resource File");
        file = filechooser.showOpenDialog(stage);

        if(file != null){
            pathLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void encryptFile(ActionEvent event){
        if(file == null){
            errorMessage("Problem with input file", "Choose file first");
        }
        else if(file.length() <= 104857600 && file.length() >= 1024){

            String mode = modeChoiceBox.getValue();
            if(subBlockChoiceBox.isDisabled()){
                System.out.println(mode);
                byte[] secretKey = gienerator.generateSesionKey();
                System.out.println(Encryptor.encrypt("howtodoinjava.com",secretKey.toString(), mode));
            }
            else{
                mode = mode + subBlockChoiceBox.getValue();
                System.out.println(mode);
                System.out.println(Encryptor.encrypt("howtodoinjava.com","ssshhhhhhhhhhh!!!!", mode));
            }
        }
        else if (file.length() < 1024){
            errorMessage("Input file too small", "Choose other file with size over 1kB");
        }
        else if (file.length() > 104857600){
            errorMessage("Input file too large", "Choose other file with size less than 100MB");
        }

    }

    private void errorMessage(String header, String text){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(text);
        errorAlert.showAndWait();
    }
    public void setStage(Stage stage){
        this.stage = stage;
    }
}
