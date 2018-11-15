package ru.rakhimova.bean.remote;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rakhimova.remote.FolderRemoteService;
import ru.rakhimova.system.ApplicationService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.nodetype.NodeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class FolderRemoteServiceBean implements FolderRemoteService {

    private static final String NT_FOLDER = "nt:folder";

    private static final String PRINT_LIST = "List remote folders:";

    private static final String NEW_FOLDER_NAME = "New folder ";

    @Inject
    private ApplicationService applicationService;

    @Override
    @SneakyThrows
    public void printListFolderNameRoot() {
        System.out.println(PRINT_LIST);
        for (final Node node : getListFolderNameRoot()) {
            System.out.println(node.getPath());
        }
    }

    @Override
    public @NotNull List<Node> getListFolderNameRoot() {
        final List<Node> listFolderName;
        final Node root = applicationService.getRootNode();
        if (root == null) return Collections.emptyList();
        listFolderName = getListFolderNameService(root);
        return listFolderName;
    }

    @SneakyThrows
    private List<Node> getListFolderNameService(@NotNull final Node root) {
        final List<Node> listFolderName = new ArrayList<>();
        final NodeIterator nt = root.getNodes();
        while (nt.hasNext()) {
            final Node node = nt.nextNode();
            final NodeType nodeType = node.getPrimaryNodeType();
            final boolean isFolder = nodeType.isNodeType(NT_FOLDER);
            if (isFolder) listFolderName.add(node);
        }
        return listFolderName;
    }

    @Override
    @SneakyThrows
    public void createFolder(@Nullable String newFolderName) {
        if (newFolderName == null || newFolderName.isEmpty()) {
            int random = (int) (Math.random() * 10); //FIXME: Продумать другой алгоритм присваивания номера папки по умолчанию
            newFolderName = NEW_FOLDER_NAME + random;
        }
        final Node root = applicationService.getRootNode();
        if (root == null) return;
        root.addNode(newFolderName, NT_FOLDER);
        applicationService.save();
    }

    @Override
    @SneakyThrows
    public void deleteFolder(@Nullable final String folderName) {
        if (folderName == null || folderName.isEmpty()) return;
        final Node root = applicationService.getRootNode();
        if (root == null) return;
        final Node node = root.getNode(folderName);
        node.remove();
        applicationService.save();
    }

    @Override
    @SneakyThrows
    public void clearRoot() {
        final Node root = applicationService.getRootNode();
        final NodeIterator nt;
        if (root != null) {
            nt = root.getNodes();
            while (nt.hasNext()) {
                final Node node = nt.nextNode();
                final NodeType nodeType = node.getPrimaryNodeType();
                final boolean isFolder = nodeType.isNodeType(NT_FOLDER);
                if (isFolder) node.remove();
            }
        }
        applicationService.save();
    }

}
