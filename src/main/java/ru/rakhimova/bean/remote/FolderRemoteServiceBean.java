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

    @Inject
    private ApplicationService applicationService;

    @Override
    @SneakyThrows
    public void printListFolderNameRoot() {
        System.out.println("List folders:");
        for (Node node : getListFolderNameRoot()) System.out.println(node.getName());
    }

    @Override
    public @NotNull List<Node> getListFolderNameRoot() {
        final List<Node> listFolderName;
        final Node root = applicationService.getRootNode();
        if (root == null) return Collections.emptyList();
        listFolderName = getListFolderNameService(root);
        return listFolderName;
    }

    public @NotNull List<Node> getListFolderNameInFolder(Node checkFolder) {
        final List<Node> listFolderName;
        if (checkFolder == null) return Collections.emptyList();
        listFolderName = getListFolderNameService(checkFolder);
        return listFolderName;
    }

    @SneakyThrows
    private List<Node> getListFolderNameService(Node root) {
        final List<Node> listFolderName = new ArrayList<>();
        final NodeIterator nt = root.getNodes();
        while (nt.hasNext()) {
            final Node node = nt.nextNode();
            final NodeType nodeType = node.getPrimaryNodeType();
            final boolean isFolder = nodeType.isNodeType("nt:folder");
            if (isFolder) listFolderName.add(node);
        }
        return listFolderName;
    }

    @Override
    @SneakyThrows
    public void createFolder(String folderName) {
        @Nullable final Node root = applicationService.getRootNode();
        if (root == null) return;
        root.addNode(folderName, "nt:folder");
    }

    @Override
    @SneakyThrows
    public void deleteFolder(@Nullable final String folderName) {
        if (folderName == null || folderName.isEmpty()) return;
        final Node root = applicationService.getRootNode();
        if (root == null) return;
        final Node node = root.getNode(folderName);
        node.remove();
    }

    @Override
    @SneakyThrows
    public void clearRoot() {
        final Node root = applicationService.getRootNode();
        final NodeIterator nt = root.getNodes();
        while (nt.hasNext()) {
            final Node node = nt.nextNode();
            final NodeType nodeType = node.getPrimaryNodeType();
            final boolean isFolder = nodeType.isNodeType("nt:folder");
            if (isFolder) node.remove();
        }
    }

}
