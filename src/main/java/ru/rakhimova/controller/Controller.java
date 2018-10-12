package ru.rakhimova.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.SneakyThrows;
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

    public void addFolder(MouseEvent mouseEvent) {
        String folderName = textFieldName.getText();
        if (buttonRemote.isSelected()) {
            folderRemoteService.createFolder(folderName);
        } else folderLocalService.createFolder(folderName);
        refresh(mouseEvent);
    }

    public void addFile(MouseEvent mouseEvent) {
        final String fileName = textFieldName.getText();
        final String text = "Hello"; //FIXME
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

    private void showSyncFolder() { //FIXME
        String path = settingService.getSyncFolder();
        TreeItem<String> rootTreeNode = new TreeItem<>("SyncFolder");
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Incorrect directory name");
        }
        Deque<File> stack = new ArrayDeque<>();
        stack.add(dir);
        while (!stack.isEmpty()) {
            File element = stack.pollLast();
            if (element.isDirectory()) {
                TreeItem<String> newFolder = new TreeItem<>(element.getName());
                File[] files = element.listFiles();
                for (int i = files.length - 1; i >= 0; i--) {
                    String fileName = files[i].getName();
                    newFolder.getChildren().add(new TreeItem<>(fileName));
                    stack.add(files[i]);
                }
                if (!newFolder.getValue().equals(dir.getName())) {
                    rootTreeNode.getChildren().add(newFolder);
                }
            }
        }

        treeViewFiles.setRoot(rootTreeNode);
    }

    @SneakyThrows
    private void showRemoteRepository() { //FIXME
        TreeItem<String> rootTreeNode = new TreeItem<>("Remote repository");
        final Node root = applicationService.getRootNode();

        Deque<Node> stack = new ArrayDeque<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            Node node = stack.pollLast();
            final NodeType nodeType = node.getPrimaryNodeType();
            final boolean isFolder = nodeType.isNodeType("nt:folder");
            if (isFolder) {
                TreeItem<String> newFolder = new TreeItem<>(node.getName());
                List<Node> folders = folderRemoteService.getListFolderNameInFolder(node);
                List<Node> files = fileRemoteService.getListFileNameInFolder(node);
                for (int i = folders.size() - 1; i >= 0; i--) {
                    String fileName = folders.get(i).getName();
                    newFolder.getChildren().add(new TreeItem<>(fileName));
                    stack.add(folders.get(i));
                }
                if (!newFolder.getValue().equals(root.getName())) {
                    rootTreeNode.getChildren().add(newFolder);
                }
            }
        }

        treeViewFiles.setRoot(rootTreeNode);
    }

    public void changeFile(MouseEvent mouseEvent) {
        //TODO
    }

    public void changeLabelRemote(MouseEvent mouseEvent) {
       if (buttonRemote.isSelected()) buttonRemote.setText("Remote");
       else buttonRemote.setText("Local");
       refresh(mouseEvent);
    }

}


