package ru.rakhimova.bean.remote;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rakhimova.remote.FileRemoteService;
import ru.rakhimova.system.ApplicationService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class FileRemoteServiceBean implements FileRemoteService {

    private static final String PRINT_LIST = "List files:";

    private static final String NT_FILE = "nt:file";

    private static final String JCR_JAVA = "jcr:java";

    private static final String JCR_CONTENT = "jcr:content";

    private static final String NT_RESOURCE = "nt:resource";

    private static final String JCR_LAST_MODIFIER = "jcr:lastModifier";

    @Inject
    private ApplicationService applicationService;

    @Override
    @SneakyThrows
    public void printListFileNameRoot() {
        System.out.println(PRINT_LIST);
        for (final Node file : getListFileNameRoot()) System.out.println(file.getName());
    }

    @Override
    public @NotNull List<Node> getListFileNameRoot() {
        final List<Node> listFolderName;
        final Node root = applicationService.getRootNode();
        if (root == null) return Collections.emptyList();
        listFolderName = getListFileNameService(root);
        return listFolderName;
    }

    public @NotNull List<Node> getListFileNameInFolder(@NotNull final Node mainFolder) {
        final List<Node> listFolderName;
        listFolderName = getListFileNameService(mainFolder);
        return listFolderName;
    }

    @SneakyThrows
    private @NotNull List<Node> getListFileNameService(@NotNull final Node root) {
        final List<Node> listFolderName = new ArrayList<>();
        final NodeIterator nt = root.getNodes();
        while (nt.hasNext()) {
            final Node node = nt.nextNode();
            final NodeType nodeType = node.getPrimaryNodeType();
            final boolean isFile = nodeType.isNodeType(NT_FILE);
            if (isFile) listFolderName.add(node);
        }
        return listFolderName;
    }

    @Override
    @SneakyThrows
    public void clearRoot() {
        final Node root = applicationService.getRootNode();
        if (root == null) return;
        final NodeIterator nt = root.getNodes();
        while (nt.hasNext()) {
            final Node node = nt.nextNode();
            final NodeType nodeType = node.getPrimaryNodeType();
            final boolean isFile = nodeType.isNodeType(NT_FILE);
            if (isFile) node.remove();
        }
    }

    @Override
    @SneakyThrows
    public @Nullable byte[] readData(@Nullable final String name) {
        if (name == null || name.isEmpty()) return new byte[]{};
        final Node root = applicationService.getRootNode();
        if (root == null) return new byte[]{};
        final Node node = root.getNode(name);
        final Binary binary = node.getProperty(JCR_JAVA).getBinary();
        return IOUtils.toByteArray(binary.getStream());
    }

    @Override
    @SneakyThrows
    public void writeData(@Nullable final String name, @Nullable final byte[] data) {
        if (name == null || name.isEmpty()) return;
        final Session session = applicationService.session();
        if (session == null) return;
        final Node root = session.getRootNode();
        final Node file = root.addNode(name, NT_FILE);
        final Node contentNode = file.addNode(JCR_CONTENT, NT_RESOURCE);
        final ByteArrayInputStream stream = new ByteArrayInputStream(data);
        final Binary binary = session.getValueFactory().createBinary(stream);
        contentNode.setProperty(JCR_JAVA, binary);
        final Calendar created = Calendar.getInstance();
        contentNode.setProperty(JCR_LAST_MODIFIER, created); //FIXME
    }

    @Override
    @SneakyThrows
    public boolean exist(@Nullable final String name) {
        if (name == null || name.isEmpty()) return false;
        final Node root = applicationService.getRootNode();
        if (root == null) return false;
        return root.hasNode(name);
    }

    @Override
    @SneakyThrows
    public void deleteFile(@Nullable final String name) {
        if (name == null || name.isEmpty()) return;
        final Node root = applicationService.getRootNode();
        if (root == null) return;
        final Node node = root.getNode(name);
        node.remove();
    }

    @Override
    @SneakyThrows
    public void createTextFile(@Nullable final String fileName, @Nullable final String text) {
        if (text == null) return;
        writeData(fileName, text.getBytes());
    }

}
