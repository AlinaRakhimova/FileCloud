package ru.rakhimova.local;

import org.jetbrains.annotations.NotNull;
import ru.rakhimova.basic.FolderService;

import java.util.List;

public interface FolderLocalService extends FolderService {

    void init();

    @NotNull
    List<String> getListFolderNameRoot();

}
