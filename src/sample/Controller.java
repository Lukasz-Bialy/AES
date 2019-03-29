package sample;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    public void setStage(Stage stage){
        this.stage = stage;
    }
}
