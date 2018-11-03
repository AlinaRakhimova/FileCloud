package ru.rakhimova.GUI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.bean.remote.FileRemoteServiceBean;
import ru.rakhimova.bean.remote.FolderRemoteServiceBean;
import ru.rakhimova.bean.service.SyncServiceBean;

import javax.enterprise.inject.spi.CDI;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.nodetype.NodeType;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class MainController {

    private static final String LABEL_ROOT_REMOTE_REPOSITORY = "Remote repository";

    private static final String FXML_IMPORT_FILE_FRAME = "/fxml/importFile.fxml";

    private static final String TITLE = "Import file";

    private static final String RIGHT_SEPARATOR = "/";

    private static final String NT_FOLDER = "nt:folder";

    private static final String LABEL_REMOTE_PATH_FXML = "#labelRemotePath";

    private FolderRemoteServiceBean folderRemoteService = CDI.current().select(FolderRemoteServiceBean.class).get();

    private FileRemoteServiceBean fileRemoteService = CDI.current().select(FileRemoteServiceBean.class).get();

    @FXML
    private TextField textFieldName;

    @FXML
    private TreeView<String> treeViewFiles;

    @FXML
    private Label labelPath;

    private String remoteFilePath;

    public void addFolder() {
        final String textLabelPath = labelPath.getText();
        String folderName;
        String path;
        if ((textLabelPath).equals(LABEL_ROOT_REMOTE_REPOSITORY)) path = "";
        else path = textLabelPath.substring(LABEL_ROOT_REMOTE_REPOSITORY.length() + 1) + RIGHT_SEPARATOR;
        folderName = path + textFieldName.getText();
        folderRemoteService.createFolder(folderName);
        refresh();
    }

    public void deleteFolder() {
        final String textLabelPath = labelPath.getText();
        String path;
        path = textLabelPath.substring(LABEL_ROOT_REMOTE_REPOSITORY.length() + 1);
        folderRemoteService.deleteFolder(path);
        refresh();
    }

    @SneakyThrows
    public void addFile() {
        String textLabelPath = labelPath.getText();
        if ("".equals(textLabelPath)) textLabelPath = LABEL_ROOT_REMOTE_REPOSITORY;
        String path;
        if ((textLabelPath).equals(LABEL_ROOT_REMOTE_REPOSITORY)) path = "";
        else path = textLabelPath.substring(LABEL_ROOT_REMOTE_REPOSITORY.length() + 1) + RIGHT_SEPARATOR;
        this.remoteFilePath = path;
        initStageImportFile();
    }

    @SneakyThrows
    private void initStageImportFile() {
        final Stage stage = new Stage();
        final Parent root = FXMLLoader.load(getClass().getResource(FXML_IMPORT_FILE_FRAME));
        final Scene scene = new Scene(root, 400, 130);
        final Label labelRemoteFilePath = (Label) scene.lookup(LABEL_REMOTE_PATH_FXML);
        labelRemoteFilePath.setText(this.remoteFilePath);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void deleteFile() {
        final String textLabelPath = labelPath.getText();
        final String path = textLabelPath.substring(LABEL_ROOT_REMOTE_REPOSITORY.length() + 1);
        fileRemoteService.deleteFile(path);
        refresh();
    }

    @SneakyThrows
    public void refresh() {
        showRemoteRepository();
    }

    @SneakyThrows
    private void showRemoteRepository() {
        final TreeItem<String> rootTreeNode = new TreeItem<>(LABEL_ROOT_REMOTE_REPOSITORY);
        final List<Node> remoteListFolders = folderRemoteService.getListFolderNameRoot();
        final Deque<Node> stackRemote = new ArrayDeque<>(remoteListFolders);
        while (!stackRemote.isEmpty()) {
            final Node newFolder = stackRemote.pollLast();
            if (newFolder != null) addNode(newFolder, rootTreeNode);
        }
        treeViewFiles.setRoot(rootTreeNode);
    }

    @SneakyThrows
    private void addNode(@NotNull final Node newFolder, @NotNull TreeItem<String> rootTreeNode) {
        final TreeItem<String> itemFolder = new TreeItem<>(newFolder.getName());
        final NodeIterator nodeIterator = newFolder.getNodes();
        while (nodeIterator.hasNext()) {
            final Node node = nodeIterator.nextNode();
            final NodeType nodeType = node.getPrimaryNodeType();
            final boolean isFolder = nodeType.isNodeType(NT_FOLDER);
            final TreeItem<String> chItemFolder = new TreeItem<>(node.getName());
            if (isFolder) addNode(node, itemFolder);
            else itemFolder.getChildren().add(chItemFolder);
        }
        rootTreeNode.getChildren().add(itemFolder);
    }

    public void choosePath() {
        final MultipleSelectionModel<TreeItem<String>> selectionModel = treeViewFiles.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((changed, oldValue, newValue) -> {
            if (newValue != null) {
                String path = newValue.getValue();
                TreeItem<String> parent = newValue.getParent();
                while (parent != null) {
                    path = parent.getValue() + RIGHT_SEPARATOR + path;
                    parent = parent.getParent();
                }
                labelPath.setText(path);
            }
        });
    }

    public void onSynchronize() {
        final SyncServiceBean syncService = new SyncServiceBean();
        syncService.sync();
    }

}