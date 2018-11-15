package ru.rakhimova.GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.bean.local.FileLocalServiceBean;
import ru.rakhimova.bean.remote.FileRemoteServiceBean;
import ru.rakhimova.bean.service.SettingServiceBean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import java.io.File;

@ApplicationScoped
public class ImportFile {

    private static final String LEFT_SEPARATOR = "\\";

    @FXML
    private Button buttonFileChooser;

    @FXML
    private TextField textFieldChooseFile;

    @FXML
    private Label labelRemotePath;

    private FileLocalServiceBean fileLocalService = CDI.current().select(FileLocalServiceBean.class).get();

    private FileRemoteServiceBean fileRemoteService = CDI.current().select(FileRemoteServiceBean.class).get();

    private SettingServiceBean settingService = CDI.current().select(SettingServiceBean.class).get();

    private Stage stage;

    public void chooseFile() {
        final FileChooser fileChooser = new FileChooser();
        configuringFileChooser(fileChooser);
        buttonFileChooser.setOnAction((ActionEvent event) -> {
            final File file = fileChooser.showOpenDialog(stage);
            textFieldChooseFile.setText(file.getPath());
        });
    }

    public void doImport() {
        final String filePath = textFieldChooseFile.getText();
        final String fileName = filePath.substring(filePath.lastIndexOf(LEFT_SEPARATOR) + 1);
        final byte[] data = fileLocalService.readDataToImport(filePath);
        final String remoteFilePath = labelRemotePath.getText();
        textFieldChooseFile.getScene().getWindow().hide();
        fileRemoteService.writeData(remoteFilePath + fileName, data);
    }

    private void configuringFileChooser(@NotNull final FileChooser fileChooser) {
        fileChooser.setTitle("Select file");
        fileChooser.setInitialDirectory(new File(settingService.getSyncFolder()));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("DOCX", "*.docx"));
    }

}
