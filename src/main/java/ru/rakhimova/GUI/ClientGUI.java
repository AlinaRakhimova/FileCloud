package ru.rakhimova.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.bean.tray.SystemTrayBean;

public class ClientGUI extends Application {

    private static final String TRAY_ICON_PNG = "/image/trayIcon.png";

    private static final String TITLE = "FileCloud";

    private static final String FXML_MAIN_FRAME = "/fxml/mainFrame.fxml";

    private static final String CSS_STYLE_MAIN_FRAME = "css/styleMainFrame.css";

    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource(FXML_MAIN_FRAME));
        final Scene scene = new Scene(root, 730, 350);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add(CSS_STYLE_MAIN_FRAME);
        primaryStage.getIcons().add(new javafx.scene.image.Image(TRAY_ICON_PNG));
        primaryStage.show();

        SystemTrayBean systemTray = new SystemTrayBean();
        systemTray.addAppToTray(primaryStage);
    }

    public void startUI() {
        launch(ClientGUI.class);
    }
}
