package ru.rakhimova.bean.remote;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rakhimova.bean.service.ApplicationServiceBean;
import ru.rakhimova.remote.FolderRemoteService;
import ru.rakhimova.system.ApplicationService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class FolderRemoteServiceBean implements FolderRemoteService {

    private static final Logger LOGGER = Logger.getLogger(FolderRemoteServiceBean.class.getSimpleName());

    @Inject
    private ApplicationService applicationService;

    @Override
    public void printListFolderNameRoot() {
        for (String name : getListFolderNameRoot()) System.out.println(name);
    }

    @Override
    public @NotNull List<String> getListFolderNameRoot() {
        final List<String> listFolderName = new ArrayList<>();
        final Node root = applicationService.getRootNode();
        if (root == null) return Collections.emptyList(); //?
        try {
            final NodeIterator nt = root.getNodes();
            while (nt.hasNext()) {
                final Node node = nt.nextNode();
                final NodeType nodeType = node.getPrimaryNodeType();
                final boolean isFolder = nodeType.isNodeType("nt:folder");
                if (isFolder) listFolderName.add(node.getName());
            }
            return listFolderName;
        } catch (final RepositoryException e) {
            LOGGER.severe(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void createFolder(String folderName) {
        @Nullable final Node root = applicationService.getRootNode();
        System.out.println(root);
        if (root == null) return;
        try {
            root.addNode(folderName, "nt:folder");
        } catch (RepositoryException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Override
    @SneakyThrows
    public void deleteFolder(@Nullable final String folderName) {
        if (folderName == null || folderName.isEmpty()) return;
        try {
            final Node root = applicationService.getRootNode();
            final Node node = root.getNode(folderName);
            node.remove();
        } catch (RepositoryException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Override
    @SneakyThrows
    public void clearRoot() {
        final Node root = applicationService.getRootNode();
        try {
            final NodeIterator nt = root.getNodes();
            while (nt.hasNext()) {
                final Node node = nt.nextNode();
                final NodeType nodeType = node.getPrimaryNodeType();
                final boolean isFolder = nodeType.isNodeType("nt:folder");
                if (isFolder) node.remove();
            }
        } catch (final RepositoryException e) {
            LOGGER.severe(e.getMessage());
        }
    }

}
