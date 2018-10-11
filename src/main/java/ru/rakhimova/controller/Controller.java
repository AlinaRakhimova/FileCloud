package ru.rakhimova.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import ru.rakhimova.bean.local.FileLocalServiceBean;
import ru.rakhimova.bean.local.FolderLocalServiceBean;
import ru.rakhimova.bean.remote.FolderRemoteServiceBean;
import ru.rakhimova.bean.service.SettingServiceBean;
import ru.rakhimova.local.FileLocalService;
import ru.rakhimova.local.FolderLocalService;
import ru.rakhimova.remote.FileRemoteService;
import ru.rakhimova.remote.FolderRemoteService;
import ru.rakhimova.system.SettingService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;


public class Controller {

    @Inject
    private FolderRemoteService folderRemoteService;

    @Inject
    private FileRemoteService fileRemoteService;

    @Inject
    private FolderLocalService folderLocalService;

    @Inject
    private FileLocalService fileLocalService;

    @Inject
    private SettingService settingService;

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
        String fileName = textFieldName.getText();
        String text = "Hello"; //FIXME
        if (buttonRemote.isSelected()) {
            fileRemoteService.createTextFile(fileName, text);
        } else fileLocalService.createTextFile(fileName, text);
        refresh(mouseEvent);
    }

    public void deleteFile(MouseEvent mouseEvent) {
        String fileName = textFieldName.getText();
        if (buttonRemote.isSelected()) {
            fileRemoteService.deleteFile(fileName);
        } else fileLocalService.deleteFile(fileName);
        refresh(mouseEvent);
    }

    public void deleteFolder(MouseEvent mouseEvent) {
        String folderName = textFieldName.getText();
        if (buttonRemote.isSelected()) {
            folderRemoteService.deleteFolder(folderName);
        } else folderLocalService.deleteFolder(folderName);
        refresh(mouseEvent);
    }

    public void refresh(MouseEvent mouseEvent) {
        if (buttonRemote.isSelected()) {
            //TODO
        } else { //FIXME
            String path = settingService.getSyncFolder();
            TreeItem<String> rootTreeNode = new TreeItem<String>("SyncFolder");
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
    }

    public void changeFile(MouseEvent mouseEvent) {
        //TODO
    }

    public void changeLabelRemote(MouseEvent mouseEvent) {
       if (buttonRemote.isSelected()) buttonRemote.setText("Remote");
       else buttonRemote.setText("Local");
    }

}


