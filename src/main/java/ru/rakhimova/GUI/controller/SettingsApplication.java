package ru.rakhimova.GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.App;
import ru.rakhimova.annotation.Loggable;
import ru.rakhimova.bean.service.BootstrapServiceBean;

import javax.enterprise.inject.se.SeContainerInitializer;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

public class SettingsApplication {

    private static final String ENCODING = "UTF-8";

    private static final String FILE_NAME = "application.properties";
    private static final String KEY_JCR_URL = "jcr.url";
    private static final String KEY_JCR_LOGIN = "jcr.login";
    private static final String KEY_JCR_PASSWORD = "jcr.password";

    private static final String KEY_SYNC_ACTIVE = "sync.active";
    private static final String KEY_SYNC_FOLDER = "sync.folder";
    private static final String KEY_SYNC_TIMEOUT = "sync.timeout";

    private static final String SELECTOR_SYNC_FOLDER = "#textSyncFolder";

    private Stage stage;

    @FXML
    private TextField textURL;

    @FXML
    private TextField textLogin;

    @FXML
    private TextField textPassword;

    @FXML
    private TextField textTimeout;

    @FXML
    private TextField textSyncFolder;

    @FXML
    private RadioButton buttonSyncActive;

    @FXML
    private Button chooseFolder;

    @Loggable
    @SneakyThrows
    public void getSettings() {
        final String url = textURL.getText();
        final String login = textLogin.getText();
        final String password = textPassword.getText();
        final String timeout = textTimeout.getText();
        final String syncFolder = textSyncFolder.getText();
        String syncActive;
        if (buttonSyncActive.isSelected()) syncActive = "true";
        else syncActive = "false";
        final Properties properties = new Properties();
        properties.setProperty(KEY_JCR_URL, url);
        properties.setProperty(KEY_JCR_LOGIN, login);
        properties.setProperty(KEY_JCR_PASSWORD, password);
        properties.setProperty(KEY_SYNC_FOLDER, syncFolder);
        properties.setProperty(KEY_SYNC_TIMEOUT, timeout);
        properties.setProperty(KEY_SYNC_ACTIVE, syncActive);

        final ClassLoader classLoader = SettingsApplication.class.getClassLoader();
        final File settingFile = new File(classLoader.getResource(FILE_NAME).getFile());
        final FileOutputStream outputStream = new FileOutputStream(settingFile);
        properties.store(outputStream, ENCODING);
        textLogin.getScene().getWindow().setOpacity(0.0);
        initClient();
    }

    private void initClient() {
        SeContainerInitializer.newInstance().addPackages(App.class).initialize().select(BootstrapServiceBean.class).get().init();
    }

    public void chooseFolder() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        configuringFileChooser(directoryChooser);
        chooseFolder.setOnAction((ActionEvent event) -> {
            final File file = directoryChooser.showDialog(stage);
            final TextField syncFolderField = (TextField) stage.getScene().lookup(SELECTOR_SYNC_FOLDER);
            syncFolderField.setText(file.getPath());
        });
    }

    private void configuringFileChooser(@NotNull final DirectoryChooser directoryChooser) {
        directoryChooser.setTitle("Select folder");
    }

    public void setStage(@NotNull final Stage primaryStage) {
        stage = primaryStage;
    }
}
