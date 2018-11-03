package ru.rakhimova.GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.bean.local.FileLocalServiceBean;
import ru.rakhimova.bean.remote.FileRemoteServiceBean;

import javax.enterprise.inject.spi.CDI;
import java.io.File;

public class ImportFile {

    private static final String FXML_IMPORT_FILE_FRAME = "/fxml/importFile.fxml";

    private static final String TITLE = "Import file";

    @FXML
    private Button buttonFileChooser;

    @FXML
    private TextField textFieldChooseFile;

    private Stage stage;

    private FileLocalServiceBean fileLocalService = CDI.current().select(FileLocalServiceBean.class).get();

    private FileRemoteServiceBean fileRemoteService = CDI.current().select(FileRemoteServiceBean.class).get();

    private String remoteFilePath;

    @SneakyThrows
    public void init(@NotNull final String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
        stage = new Stage();
        final Parent root = FXMLLoader.load(getClass().getResource(FXML_IMPORT_FILE_FRAME));
        final Scene scene = new Scene(root, 400, 130);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

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
        final byte[] data = fileLocalService.readDataToImport(filePath);
        fileRemoteService.writeData(remoteFilePath, data);
    }

    private void configuringFileChooser(FileChooser fileChooser) {
        fileChooser.setTitle("Select file");
        fileChooser.setInitialDirectory(new File("./temp"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("DOCX", "*.docx"));
    }

}
