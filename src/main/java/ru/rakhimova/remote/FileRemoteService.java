package ru.rakhimova.remote;

import org.jetbrains.annotations.NotNull;
import ru.rakhimova.basic.FileService;

import javax.jcr.Node;
import java.util.List;

public interface FileRemoteService extends FileService {

    @NotNull
    List<Node> getListFileNameRoot();

}
