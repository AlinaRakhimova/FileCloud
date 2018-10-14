package ru.rakhimova.local;

import org.jetbrains.annotations.NotNull;
import ru.rakhimova.basic.FileService;

import java.util.List;

public interface FileLocalService extends FileService {

    @NotNull
    List<String> getListFileNameRoot();

}
