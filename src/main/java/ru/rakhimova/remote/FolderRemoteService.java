package ru.rakhimova.remote;

import org.jetbrains.annotations.NotNull;
import ru.rakhimova.basic.FolderService;

import javax.jcr.Node;
import java.util.List;

public interface FolderRemoteService extends FolderService {

    @NotNull
    List<Node> getListFolderNameRoot();

}
