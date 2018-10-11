package ru.rakhimova.basic;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FolderService {

    void printListFolderNameRoot();

    @NotNull
    List<String> getListFolderNameRoot();

    void createFolder(String folderName);

    void deleteFolder(String folderName);

    void clearRoot();

}
