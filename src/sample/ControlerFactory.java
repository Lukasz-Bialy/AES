package sample;

import javafx.fxml.FXMLLoader;

public class ControlerFactory {
    public Control createController(FXMLLoader fxmlLoader, String type) {
        Control controler = null;
        if(type.equals("home")) {
            controler = fxmlLoader.<Controller>getController();
        }
        else if(type.equals("client")) {
            controler = fxmlLoader.<ClientController>getController();
        }
        else if(type.equals("server")) {
            controler = fxmlLoader.<ServerController>getController();
        }
        return controler;
    }
}
