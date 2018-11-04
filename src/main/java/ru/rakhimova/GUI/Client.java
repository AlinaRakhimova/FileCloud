package ru.rakhimova.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ru.rakhimova.GUI.tray.SystemTrayBean;

public class Client {

    private static final String TRAY_ICON_PNG = "/image/trayIcon.png";

    private static final String TITLE = "FileCloud";

    private static final String FXML_MAIN_FRAME = "/fxml/mainFrame.fxml";

    private static final String CSS_STYLE_MAIN_FRAME = "css/styleMainFrame.css";

    @SneakyThrows
    public void init() {
        final Stage stage = new Stage();
        final Parent root = FXMLLoader.load(getClass().getResource(FXML_MAIN_FRAME));
        final Scene scene = new Scene(root, 730, 350);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.getScene().getStylesheets().add(CSS_STYLE_MAIN_FRAME);
        stage.getIcons().add(new javafx.scene.image.Image(TRAY_ICON_PNG));

        final SystemTrayBean systemTray = new SystemTrayBean();
        systemTray.addAppToTray(stage);
    }
}
