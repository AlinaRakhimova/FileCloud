package ru.rakhimova.bean.service;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rakhimova.GUI.tray.SystemTrayBean;
import ru.rakhimova.annotation.Loggable;
import ru.rakhimova.bean.local.FileLocalServiceBean;
import ru.rakhimova.bean.local.FolderLocalServiceBean;
import ru.rakhimova.bean.remote.FileRemoteServiceBean;
import ru.rakhimova.bean.remote.FolderRemoteServiceBean;
import ru.rakhimova.system.SyncService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.nodetype.NodeType;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@ApplicationScoped
public class SyncServiceBean implements SyncService {

    private static final String NT_FOLDER = "nt:folder";

    private static final String RIGHT_SEPARATOR = "/";

    private static final String LEFT_SEPARATOR = "\\";

    private FolderRemoteServiceBean folderRemoteService = CDI.current().select(FolderRemoteServiceBean.class).get();

    private FileRemoteServiceBean fileRemoteService = CDI.current().select(FileRemoteServiceBean.class).get();

    private FolderLocalServiceBean folderLocalService = CDI.current().select(FolderLocalServiceBean.class).get();

    private FileLocalServiceBean fileLocalService = CDI.current().select(FileLocalServiceBean.class).get();

    private SettingServiceBean settingService = CDI.current().select(SettingServiceBean.class).get();

    private ApplicationServiceBean applicationService = CDI.current().select(ApplicationServiceBean.class).get();

    private int sizeSyncDirectory;

    @Loggable
    @Override
    @SneakyThrows
    public void sync() {
        final File[] files = folderLocalService.getRoot().listFiles();
        int sizeSyncFolder = 0;
        if (files != null) sizeSyncFolder = files.length;
        final Node root = applicationService.getRootNode();
        boolean fullRepository = false;
        if (root != null) fullRepository = root.hasNodes();

        if (sizeSyncFolder == 0 && fullRepository) syncFoldersFromRemote();
        if (sizeSyncFolder != 0 && !fullRepository) syncFoldersFromLocal();
        if (sizeSyncFolder != 0 && fullRepository) synchronizeAll();
        applicationService.save();
    }

    public void showTrayMassage() {
        final SystemTrayBean trayBean = new SystemTrayBean();
        trayBean.showMessage();
    }

    private void syncFoldersFromLocal() {
        sizeSyncDirectory = settingService.getSyncFolder().length();
        final List<File> localListFolders = folderLocalService.getListFolderNameRoot();
        final Deque<File> nonexistentFolders = new ArrayDeque<>(localListFolders);
        fileRemoteService.clearRoot();
        folderRemoteService.clearRoot();
        while (!nonexistentFolders.isEmpty()) {
            final File newFolder = nonexistentFolders.pollLast();
            if (newFolder == null) return;
            final String pathLocal = newFolder.getPath();
            final String pathRemote = pathLocal.substring(sizeSyncDirectory).replace(LEFT_SEPARATOR, RIGHT_SEPARATOR);
            folderRemoteService.createFolder(pathRemote);
            createSubFoldersLocal(nonexistentFolders, newFolder);
        }
    }

    @SneakyThrows
    private void createSubFoldersLocal(@NotNull Deque<File> nonexistentFolders, @NotNull final File newFolder) {
        final File[] files = newFolder.listFiles();
        if (files == null) return;
        for (final File file : files) {
            final String pathFile = file.getPath().substring(sizeSyncDirectory);
            if (file.isDirectory()) {
                nonexistentFolders.add(file);
            } else {
                final String pathRemote = pathFile.replace(LEFT_SEPARATOR, RIGHT_SEPARATOR);
                final byte[] data = fileLocalService.readData(pathFile);
                fileRemoteService.writeData(pathRemote, data);
            }
        }
    }

    @SneakyThrows
    @Loggable
    private void syncFoldersFromRemote() {
        final List<Node> remoteListFolders = folderRemoteService.getListFolderNameRoot();
        final Deque<Node> nonexistentFolders = new ArrayDeque<>(remoteListFolders);
        fileLocalService.clearRoot();
        folderLocalService.clearRoot(); //FIXME: Не проиходит удаление папок. Предположительно из-за наличия вложенных файлов
        while (!nonexistentFolders.isEmpty()) {
            final Node newFolder = nonexistentFolders.pollLast();
            if (newFolder != null) {
                folderLocalService.createFolder(newFolder.getParent().getPath() + RIGHT_SEPARATOR + newFolder.getName());
            }
            createSubFolders(nonexistentFolders, newFolder);
        }
    }

    @SneakyThrows
    private void createSubFolders(@NotNull Deque<Node> nonexistentFolders, @Nullable final Node newFolder) {
        final NodeIterator nodeIterator;
        if (newFolder != null) {
            nodeIterator = newFolder.getNodes();
            while (nodeIterator.hasNext()) {
                final Node node = nodeIterator.nextNode();
                final NodeType nodeType = node.getPrimaryNodeType();
                final boolean isFolder = nodeType.isNodeType(NT_FOLDER);
                final String nameFile = node.getParent().getPath() + RIGHT_SEPARATOR + node.getName();
                if (isFolder) {
                    nonexistentFolders.add(node);
                } else {
                    final String name = nameFile.substring(1);
                    final byte[] data = fileRemoteService.readData(name); //FIXME: Ошибка при чтении файла.
                    fileLocalService.writeData(nameFile, data);
                }
            }
        }
    }

    private void synchronizeAll() {
        //TODO: Сделать синхронизацию при условии заполненности локального и удаленного репозитория
        System.out.println("Do synchronize all");
    }

}
