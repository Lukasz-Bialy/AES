package sample;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Window {

    static Stage stage;

    private ControlerFactory controlerFactory;

    public Window() {
        controlerFactory = new ControlerFactory();
    }

    public void setStage(Stage stage) {
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

    private void launchView(String fxmlFile, String controlerType) {
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
        newStage.setTitle("RSATest Keys");

        VBox layout = new VBox();

        Label nameDesc = new Label("Podaj uzytkownika");
        nameDesc.setTranslateX(width / 2 - 60);
        nameDesc.setTranslateY(height / 2 - 80);

        TextField nameField = new TextField();
        nameField.setMaxWidth(150);
        nameField.setTranslateX(width / 2 - 75);
        nameField.setTranslateY(height / 2 - 70);

        Label passDesc = new Label("Podaj haslo");
        passDesc.setTranslateX(width / 2 - 60);
        passDesc.setTranslateY(height / 2 - 60);

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(150);
        passwordField.setTranslateX(width / 2 - 75);
        passwordField.setTranslateY(height / 2 - 50);

        Button generujButton = new Button();
        generujButton.setText("Generuj");
        generujButton.setPrefWidth(80);
        generujButton.setTranslateX(width / 2 - 40);
        generujButton.setTranslateY(height / 2 - 40);
        generujButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try {
                            createRSAKeys(nameField.getText(), passwordField.getText());
                            Stage stage = (Stage) generujButton.getScene().getWindow();
                            stage.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        layout.getChildren().addAll(nameDesc, nameField, passDesc, passwordField, generujButton);
        Scene stageScene = new Scene(layout, width, height);
        newStage.setScene(stageScene);
        newStage.show();
    }

    private boolean createRSAKeys(String name, String password) throws IOException, NoSuchAlgorithmException {
        KeyPair kp = RSA.generateKeyPair();//Generowanie kluczy do uzytkownika (Tworzy dwa pliki Publicuser.key i Privateuser.key w folderze projektu)
        byte[] encryptedPrivateKey = AES.encrypt(RSA.sha256(password), kp.getPrivate().getEncoded());
        RSA.toFile(kp.getPublic().getEncoded(), "/home/lukasz/IdeaProjects/BSK/PublicKeys/Public" + name);
        RSA.toFile(encryptedPrivateKey, "/home/lukasz/IdeaProjects/BSK/PrivateKeys/Private" + name);
        return true;
    }

}
