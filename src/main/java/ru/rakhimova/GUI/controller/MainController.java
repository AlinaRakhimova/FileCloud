package ru.rakhimova.GUI.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.bean.local.FileLocalServiceBean;
import ru.rakhimova.bean.local.FolderLocalServiceBean;
import ru.rakhimova.bean.remote.FileRemoteServiceBean;
import ru.rakhimova.bean.remote.FolderRemoteServiceBean;
import ru.rakhimova.bean.service.SettingServiceBean;
import ru.rakhimova.bean.service.SyncServiceBean;

import javax.enterprise.inject.spi.CDI;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.nodetype.NodeType;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class MainController {

    private static final String BUTTON_LABEL_REMOTE = "Remote";

    private static final String BUTTON_LABEL_LOCAL = "Local";

    private static final String LABEL_ROOT_REMOTE_REPOSITORY = "Remote repository";

    private static final String LABEL_SYNC_FOLDER = "SyncFolder";

    private static final String RIGHT_SEPARATOR = "/";

    private static final String NT_FOLDER = "nt:folder";

    private FolderRemoteServiceBean folderRemoteService = CDI.current().select(FolderRemoteServiceBean.class).get();

    private FileRemoteServiceBean fileRemoteService = CDI.current().select(FileRemoteServiceBean.class).get();

    private FolderLocalServiceBean folderLocalService = CDI.current().select(FolderLocalServiceBean.class).get();

    private FileLocalServiceBean fileLocalService = CDI.current().select(FileLocalServiceBean.class).get();

    private SettingServiceBean settingService = CDI.current().select(SettingServiceBean.class).get();

    @FXML
    private TextField textFieldName;

    @FXML
    private TreeView<String> treeViewFiles;

    @FXML
    private ToggleButton buttonRemote;

    @FXML
    private MenuItem addFiles;

    @FXML
    private Label labelPath;

    private TreeItem<String> rootTreeNode;

    public void addFolder() {
        final String textLabelPath = labelPath.getText();
        final String syncFolder = settingService.getSyncFolder();
        String folderName;
        String path;
        if (buttonRemote.isSelected()) {
            if ((textLabelPath).equals(LABEL_ROOT_REMOTE_REPOSITORY)) path = "";
            else path = textLabelPath.substring(LABEL_ROOT_REMOTE_REPOSITORY.length() + 1) + RIGHT_SEPARATOR;
            folderName = path + textFieldName.getText();
            folderRemoteService.createFolder(folderName);
        } else {
            if ((textLabelPath + RIGHT_SEPARATOR).equals(LABEL_SYNC_FOLDER + syncFolder.substring(1))) path = "";
            else path = textLabelPath.substring(LABEL_SYNC_FOLDER.length() + syncFolder.length() - 1);
            folderName = path + RIGHT_SEPARATOR + textFieldName.getText();
            folderLocalService.createFolder(folderName);
        }
        refresh();
    }

    public void deleteFolder() {
        final String textLabelPath = labelPath.getText();
        final String syncFolder = settingService.getSyncFolder();
        String path;
        if (buttonRemote.isSelected()) {
            path = textLabelPath.substring(LABEL_ROOT_REMOTE_REPOSITORY.length() + 1);
            folderRemoteService.deleteFolder(path);
        } else {
            path = textLabelPath.substring(LABEL_SYNC_FOLDER.length() + syncFolder.length() - 1);
            folderLocalService.deleteFolder(path);
        }
        refresh();
    }

    @SneakyThrows
    public void addFile() {
        final String textLabelPath = labelPath.getText();
        String remoteFilePath;
        String path;
        if (buttonRemote.isSelected()) {
            if ((textLabelPath).equals(LABEL_ROOT_REMOTE_REPOSITORY)) path = "";
            else path = textLabelPath.substring(LABEL_ROOT_REMOTE_REPOSITORY.length() + 1) + RIGHT_SEPARATOR;
            remoteFilePath = path + textFieldName.getText();
            ImportFile importFile = new ImportFile();
            importFile.init(remoteFilePath);
        }
    }

    public void deleteFile() {
        String textLabelPath = labelPath.getText();
        String path = textLabelPath.substring(LABEL_SYNC_FOLDER.length() + settingService.getSyncFolder().length() - 1);
        if (buttonRemote.isSelected()) {
            fileRemoteService.deleteFile(path);
        } else fileLocalService.deleteFile(path);
        refresh();
    }

    @SneakyThrows
    public void refresh() {
        if (buttonRemote.isSelected()) {
            showRemoteRepository();
        } else showSyncFolder();
    }

    private void showSyncFolder() { //FIXME: Задваивается отображение папок
        final String path = settingService.getSyncFolder();
        rootTreeNode = new TreeItem<>(LABEL_SYNC_FOLDER);
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Incorrect directory name");
        }
        Deque<File> stack = new ArrayDeque<>();
        stack.add(dir);
        while (!stack.isEmpty()) {
            File element = stack.pollLast();
            if (element == null) continue;
            addElementLocal(element, rootTreeNode);
        }
        treeViewFiles.setRoot(rootTreeNode);
    }

    @SneakyThrows
    private void addElementLocal(@NotNull final File element, TreeItem<String> rootTree) {
        if (element.isDirectory()) {
            TreeItem<String> newFolder = new TreeItem<>(element.getName());
            final File[] files = element.listFiles();
            if (files == null) return;
            for (File file : files) {
                final TreeItem<String> subLeafs = new TreeItem<>(file.getName());
                newFolder.getChildren().add(subLeafs);
                if (file.isDirectory()) {
                    addElementLocal(file, subLeafs);
                }
            }
            rootTree.getChildren().add(newFolder);
        }
    }

    @SneakyThrows
    private void showRemoteRepository() { //FIXME: Задваивается отображение папок
        rootTreeNode = new TreeItem<>(LABEL_ROOT_REMOTE_REPOSITORY);
        final List<Node> remoteListFolders = folderRemoteService.getListFolderNameRoot();
        Deque<Node> stackRemote = new ArrayDeque<>(remoteListFolders);
        while (!stackRemote.isEmpty()) {
            final Node newFolder = stackRemote.pollLast();
            if (newFolder != null) addNode(newFolder, rootTreeNode);
        }
        treeViewFiles.setRoot(rootTreeNode);
    }

    @SneakyThrows
    private void addNode(@NotNull final Node newFolder, @NotNull TreeItem<String> rootTreeNode) {
        TreeItem<String> itemFolder = new TreeItem<>(newFolder.getName());
        final NodeIterator nodeIterator = newFolder.getNodes();
        while (nodeIterator.hasNext()) {
            final Node node = nodeIterator.nextNode();
            final NodeType nodeType = node.getPrimaryNodeType();
            final boolean isFolder = nodeType.isNodeType(NT_FOLDER);
            final TreeItem<String> chItemFolder = new TreeItem<>(node.getName());
            itemFolder.getChildren().add(chItemFolder);
            if (isFolder) addNode(node, itemFolder);
        }
        rootTreeNode.getChildren().add(itemFolder);
    }

    public void choosePath() {
        MultipleSelectionModel<TreeItem<String>> selectionModel = treeViewFiles.getSelectionModel();
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

    public void changeLabelRemote() {
        if (buttonRemote.isSelected()) {
            buttonRemote.setText(BUTTON_LABEL_REMOTE);
            addFiles.setVisible(true);
        } else {
            buttonRemote.setText(BUTTON_LABEL_LOCAL);
            addFiles.setVisible(false);
        }
        refresh();
    }

    public void onSynchronize() {
        SyncServiceBean syncService = new SyncServiceBean();
        syncService.sync();
    }

}