package ru.rakhimova.basic;

import org.jetbrains.annotations.Nullable;

public interface FileService {

    void printListFileNameRoot();

    void clearRoot();

    @Nullable
    byte[] readData(String name);

    void writeData(String name, byte[] data);

    boolean exist(String name);

    void deleteFile(String name);

    void createTextFile(String name, String text);

}
