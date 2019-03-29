package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("scene.fxml"));
        Parent root = (Parent) fxmlLoader.load();

        Window window = new Window();
        window.setStage(primaryStage);

        Controller controller = fxmlLoader.<Controller>getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Bezpieczenstwo systemow komputerowych");
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();

    }


    public static void main(String[] args) { launch(args);
    }
}
