package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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

    public void launchKeyGenerator() {
        int width = 200;
        int height = 200;
        Stage newStage = new Stage();
        newStage.setTitle("RSA Keys");

        VBox layout = new VBox();

        Label desc = new Label("Podaj uzytkownika");
        desc.setTranslateX(width/2-60);
        desc.setTranslateY(height/2-50);

        TextField nameField = new TextField();
        nameField.setMaxWidth(150);
        nameField.setTranslateX(width/2-75);
        nameField.setTranslateY(height/2-40);

        Button generujButton = new Button();
        generujButton.setText("Generuj");
        generujButton.setPrefWidth(80);
        generujButton.setTranslateX(width/2-40);
        generujButton.setTranslateY(height/2);
        generujButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try {
                            RSA.generateKeyPair(nameField.getText());//Generowanie kluczy do uzytkownika (Tworzy dwa pliki Publicuser.key i Privateuser.key w folderze projektu)
                            Stage stage = (Stage) generujButton.getScene().getWindow();
                            stage.close();;
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        layout.getChildren().addAll(desc, nameField, generujButton);
        Scene stageScene = new Scene(layout, width, height);
        newStage.setScene(stageScene);
        newStage.show();
    }
}
