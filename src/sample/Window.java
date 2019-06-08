package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;

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
        newStage.setTitle("Generate RSA Keys");

        VBox layout = new VBox();

        Label nameDesc = new Label("Podaj nazwe uzytkownika");
        nameDesc.setTranslateX(width / 2 - 80);
        nameDesc.setTranslateY(height / 2 - 80);

        TextField nameField = new TextField();
        nameField.setMaxWidth(150);
        nameField.setTranslateX(width / 2 - 75);
        nameField.setTranslateY(height / 2 - 70);

        Label passDesc = new Label("Podaj haslo");
        passDesc.setTranslateX(width / 2 - 45);
        passDesc.setTranslateY(height / 2 - 60);

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(150);
        passwordField.setTranslateX(width / 2 - 75);
        passwordField.setTranslateY(height / 2 - 50);

        Button generujButton = new Button();
        generujButton.setText("Generate");
        generujButton.setPrefWidth(80);
        generujButton.setTranslateX(width / 2 - 40);
        generujButton.setTranslateY(height / 2 - 40);
        generujButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try {
                            createRSAKeys(nameField.getText(), passwordField.getText());
                            addUser(nameField.getText());
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

    private boolean createRSAKeys(String name, String password) throws Exception {
        KeyPair kp = RSA.generateKeyPair();//Generowanie kluczy do uzytkownika (Tworzy dwa pliki PublicUser.key i PrivateUser.key w folderze projektu)
        byte[] encryptedPrivateKey = AES.encrypt(RSA.sha256(password), kp.getPrivate().getEncoded());
        RSA.toFile(kp.getPublic().getEncoded(), "/home/lukasz/IdeaProjects/BSK/PublicKeys/Public" + name);
        RSA.toFile(encryptedPrivateKey, "/home/lukasz/IdeaProjects/BSK/PrivateKeys/Private" + name);
        return true;
    }

    private void addUser(String user) throws Exception {
        File file = new File("/home/lukasz/IdeaProjects/BSK/users.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        writer.write(user);
        writer.newLine();
        writer.close();
    }
}
