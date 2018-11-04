package ru.rakhimova.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.GUI.controller.SettingsApplication;
import ru.rakhimova.bean.service.SettingServiceBean;

import javax.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.util.Properties;

@ApplicationScoped
public class SettingsGUI extends Application {

    private static final String TRAY_ICON_PNG = "/image/trayIcon.png";

    private static final String TITLE = "FileCloud";

    private static final String FXML_SETTINGS_FRAME = "/fxml/settingsFrame.fxml";

    private static final String CSS_STYLE_MAIN_FRAME = "css/styleMainFrame.css";

    private static final String FILE_NAME = "application.properties";
    private static final String KEY_JCR_URL = "jcr.url";
    private static final String KEY_JCR_LOGIN = "jcr.login";
    private static final String KEY_JCR_PASSWORD = "jcr.password";

    private static final String KEY_SYNC_ACTIVE = "sync.active";
    private static final String KEY_SYNC_FOLDER = "sync.folder";
    private static final String KEY_SYNC_TIMEOUT = "sync.timeout";

    private static final String JCR_URL_DEFAULT = "localhost";
    private static final String JCR_LOGIN_DEFAULT = "admin";
    private static final String JSC_PASSWORD_DEFAULT = "admin";
    private static final String SYNC_FOLDER_DEFAULT = "./temp/";
    private static final String URL_SELECTOR = "#textURL";

    private static final String SELECTOR_LOGIN = "#textLogin";
    private static final String SELECTOR_PASSWORD = "#textPassword";
    private static final String SELECTOR_SYNC_FOLDER = "#textSyncFolder";
    private static final String SELECTOR_TIMEOUT = "#textTimeout";
    private static final String SELECTOR_SYNC_ACTIVE = "#buttonSyncActive";

    @Override
    public void start(@NotNull final Stage primaryStage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_SETTINGS_FRAME));
        final Parent root = loader.load();
        final Scene scene = new Scene(root, 400, 450);
        restoreValues(scene);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add(CSS_STYLE_MAIN_FRAME);
        primaryStage.getIcons().add(new Image(TRAY_ICON_PNG));
        primaryStage.show();
        final SettingsApplication controller = loader.getController();
        controller.setStage(primaryStage);
    }

    @SneakyThrows
    private void restoreValues(@NotNull final Scene scene) {
        final Properties properties = new Properties();
        final ClassLoader classLoader = SettingServiceBean.class.getClassLoader();
        final InputStream inputStream = classLoader.getResourceAsStream(FILE_NAME);
        properties.load(inputStream);

        final String jcrUrl = properties.getOrDefault(KEY_JCR_URL, JCR_URL_DEFAULT).toString();
        final String jcrLogin = properties.getOrDefault(KEY_JCR_LOGIN, JCR_LOGIN_DEFAULT).toString();
        final String jcrPassword = properties.getOrDefault(KEY_JCR_PASSWORD, JSC_PASSWORD_DEFAULT).toString();
        final String syncFolder = properties.getOrDefault(KEY_SYNC_FOLDER, SYNC_FOLDER_DEFAULT).toString();
        final int syncTimeout = Integer.parseInt(properties.getOrDefault(KEY_SYNC_TIMEOUT, 10000).toString());
        final boolean syncActive = Boolean.parseBoolean(properties.getOrDefault(KEY_SYNC_ACTIVE, false).toString());

        final TextField url = (TextField) scene.lookup(URL_SELECTOR);
        url.setText(jcrUrl);
        final TextField login = (TextField) scene.lookup(SELECTOR_LOGIN);
        login.setText(jcrLogin);
        final TextField password = (TextField) scene.lookup(SELECTOR_PASSWORD);
        password.setText(jcrPassword);
        final TextField syncFolderField = (TextField) scene.lookup(SELECTOR_SYNC_FOLDER);
        syncFolderField.setText(syncFolder);
        final TextField timeout = (TextField) scene.lookup(SELECTOR_TIMEOUT);
        timeout.setText(String.valueOf(syncTimeout));
        final RadioButton active = (RadioButton) scene.lookup(SELECTOR_SYNC_ACTIVE);
        active.setSelected(syncActive);
    }

    public void startGUI() {
        launch(SettingsGUI.class);
    }
}
