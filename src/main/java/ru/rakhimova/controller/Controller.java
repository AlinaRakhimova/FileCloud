package ru.rakhimova.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.bean.local.FileLocalServiceBean;
import ru.rakhimova.bean.local.FolderLocalServiceBean;
import ru.rakhimova.bean.remote.FileRemoteServiceBean;
import ru.rakhimova.bean.remote.FolderRemoteServiceBean;
import ru.rakhimova.bean.service.SettingServiceBean;

import javax.enterprise.inject.spi.CDI;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.nodetype.NodeType;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Controller {

    private static final String BUTTON_LABEL_REMOTE = "Remote";
    private static final String BUTTON_LABEL_LOCAL = "Local";
    private static final String LABEL_ROOT_REMOTE_REPOSITORY = "Remote repository";
    private static final String LABEL_SYNC_FOLDER = "SyncFolder";

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

    private TreeItem<String> rootTreeNode;

    public void addFolder(MouseEvent mouseEvent) {
        final String folderName = textFieldName.getText();
        if (buttonRemote.isSelected()) {
            folderRemoteService.createFolder(folderName);
        } else folderLocalService.createFolder(folderName);
        refresh(mouseEvent);
    }

    public void addFile(MouseEvent mouseEvent) {
        final String fileName = textFieldName.getText();
        final String text = "Hello"; //FIXME: Реализовать редактирование содержимого файла
        if (buttonRemote.isSelected()) {
            fileRemoteService.createTextFile(fileName, text);
        } else fileLocalService.createTextFile(fileName, text);
        refresh(mouseEvent);
    }

    public void deleteFile(MouseEvent mouseEvent) {
        final String fileName = textFieldName.getText();
        if (buttonRemote.isSelected()) {
            fileRemoteService.deleteFile(fileName);
        } else fileLocalService.deleteFile(fileName);
        refresh(mouseEvent);
    }

    public void deleteFolder(MouseEvent mouseEvent) {
        final String folderName = textFieldName.getText();
        if (buttonRemote.isSelected()) {
            folderRemoteService.deleteFolder(folderName);
        } else folderLocalService.deleteFolder(folderName);
        refresh(mouseEvent);
    }

    @SneakyThrows
    public void refresh(MouseEvent mouseEvent) {
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

    public void changeFile(MouseEvent mouseEvent) {
        //TODO: Реализовать обработку выбора файла/папки
    }

    public void changeLabelRemote(MouseEvent mouseEvent) {
        if (buttonRemote.isSelected()) buttonRemote.setText(BUTTON_LABEL_REMOTE);
        else buttonRemote.setText(BUTTON_LABEL_LOCAL);
        refresh(mouseEvent);
    }

}


