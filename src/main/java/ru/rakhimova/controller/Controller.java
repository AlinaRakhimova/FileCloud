package ru.rakhimova.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.bean.local.FileLocalServiceBean;
import ru.rakhimova.bean.local.FolderLocalServiceBean;
import ru.rakhimova.bean.remote.FileRemoteServiceBean;
import ru.rakhimova.bean.remote.FolderRemoteServiceBean;
import ru.rakhimova.bean.service.ApplicationServiceBean;
import ru.rakhimova.bean.service.SettingServiceBean;

import javax.enterprise.inject.spi.CDI;
import javax.jcr.Node;
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

    private ApplicationServiceBean applicationService = CDI.current().select(ApplicationServiceBean.class).get();

    @FXML
    private TextField textFieldName;

    @FXML
    private TreeView<String> treeViewFiles;

    @FXML
    private ToggleButton buttonRemote;

    private File dir;

    private Deque<File> stack;

    private TreeItem<String> rootTreeNode;

    private Deque<Node> stackRemote;

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

    private void showSyncFolder() { //FIXME: Исправить двойное отображение папок и файлов
        final String path = settingService.getSyncFolder();
        rootTreeNode = new TreeItem<>(LABEL_SYNC_FOLDER);
        dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Incorrect directory name");
        }
        stack = new ArrayDeque<>();
        stack.add(dir);
        while (!stack.isEmpty()) {
            File element = stack.pollLast();
            if (element == null) continue;
            addElementLocal(element);
        }
        treeViewFiles.setRoot(rootTreeNode);
    }

    private void addElementLocal(@NotNull final File element) {
        if (element.isDirectory()) {
            TreeItem<String> newFolder = new TreeItem<>(element.getName());
            final File[] files = element.listFiles();
            if (files == null) return;
            for (int i = files.length - 1; i >= 0; i--) {
                final String fileName = files[i].getName();
                newFolder.getChildren().add(new TreeItem<>(fileName));
                stack.add(files[i]);
            }
            final String newFolderName = newFolder.getValue();
            final String syncFolder = dir.getName();
            if (!newFolderName.equals(syncFolder)) {
                rootTreeNode.getChildren().add(newFolder);
            }
        }
    }

    @SneakyThrows
    private void showRemoteRepository() { //FIXME: Не работает отобраджение в UI
        rootTreeNode = new TreeItem<>(LABEL_ROOT_REMOTE_REPOSITORY);
        final Node root = applicationService.getRootNode();
        if (root == null) return;
        folderRemoteService.printListFolderNameRoot();
        fileRemoteService.printListFileNameRoot();
        stackRemote = new ArrayDeque<>();
        stackRemote.add(root);
        while (!stack.isEmpty()) {
            final Node node = stackRemote.pollLast();
            if (node == null) continue;
            addNode(root, node);
        }
        treeViewFiles.setRoot(rootTreeNode);
    }

    @SneakyThrows
    private void addNode(@NotNull final Node root, @NotNull final Node node) {
        final NodeType nodeType = node.getPrimaryNodeType();
        final boolean isFolder = nodeType.isNodeType(NT_FOLDER);
        if (isFolder) {
            TreeItem<String> newFolder = new TreeItem<>(node.getName());
            final List<Node> folders = folderRemoteService.getListFolderNameInFolder(node);
            final List<Node> files = fileRemoteService.getListFileNameInFolder(node);
            for (int i = folders.size() - 1; i >= 0; i--) {
                String fileName = folders.get(i).getName();
                newFolder.getChildren().add(new TreeItem<>(fileName));
                stackRemote.add(folders.get(i));
            }
            for (int i = files.size() - 1; i >= 0; i--) {
                String fileName = files.get(i).getName();
                newFolder.getChildren().add(new TreeItem<>(fileName));
            }
            final String newFolderName = newFolder.getValue();
            final String rootNodeName = root.getName();
            if (!newFolderName.equals(rootNodeName)) {
                rootTreeNode.getChildren().add(newFolder);
            }
        }
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


