package ru.rakhimova.basic;

public interface FolderService {

    void printListFolderNameRoot();

    void createFolder(String folderName);

    void deleteFolder(String folderName);

    void clearRoot();

}
