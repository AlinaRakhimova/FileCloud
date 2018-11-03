package ru.rakhimova.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.bean.tray.SystemTrayBean;

public class ClientGUI extends Application {

    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource("/fxml/mainFrame.fxml"));
        final Scene scene = new Scene(root, 730, 350);
        primaryStage.setTitle("FileCloud");
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add("css/styleMainFrame.css");
        primaryStage.show();

        SystemTrayBean systemTray = new SystemTrayBean();
        systemTray.addAppToTray(primaryStage);
    }

    public void startUI() {
        launch(ClientGUI.class);
    }
}
