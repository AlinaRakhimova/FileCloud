package ru.rakhimova.basic;

import org.jetbrains.annotations.NotNull;

import javax.jcr.Node;
import java.util.List;

public interface FolderService {

    void printListFolderNameRoot();

    @NotNull
    List<Node> getListFolderNameRoot();

    void createFolder(String folderName);

    void deleteFolder(String folderName);

    void clearRoot();

}
