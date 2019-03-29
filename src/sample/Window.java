package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Window {

    static Stage stage;

    private ControlerFactory controlerFactory;

    public Window() {
        controlerFactory = new ControlerFactory();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void launchHome() throws IOException {
        this.launchView("scene.fxml", "home");
    }

    public void launchClient() throws IOException {
        this.launchView("client.fxml", "client");
    }

    public void launchServer() throws IOException {
        this.launchView("server.fxml", "server");
    }

    private void launchView(String fxmlFile, String controlerType){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        try {
            Parent root = (Parent) fxmlLoader.load();
            Control controller = controlerFactory.createController(fxmlLoader, controlerType);
            controller.setStage(stage);
            stage.setScene(new Scene(root, 640, 480));
        } catch (IOException e) {
            System.out.println("Problem with loading fxml");
        }
    }
}
