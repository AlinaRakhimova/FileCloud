package ru.rakhimova.basic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.jcr.Node;
import java.util.List;

public interface FileService {

    @NotNull
    List<Node> getListFileNameRoot();

    void printListFileNameRoot();

    void clearRoot();

    @Nullable
    byte[] readData(String name);

    void writeData(String name, byte[] data);

    boolean exist(String name);

    void deleteFile(String name);

    void createTextFile(String name, String text);

}
