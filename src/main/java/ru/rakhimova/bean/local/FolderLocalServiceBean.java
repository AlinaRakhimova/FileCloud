package ru.rakhimova.bean.local;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rakhimova.annotation.Loggable;
import ru.rakhimova.local.FolderLocalService;
import ru.rakhimova.system.SettingService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class FolderLocalServiceBean implements FolderLocalService {

    private static final String NEW_FOLDER_NAME = "New folder ";

    private static final String PRINT_LIST = "List folders:";

    @Inject
    private SettingService settingService;

    @Loggable
    @Override
    public void init() {
        final String folderName = settingService.getSyncFolder();
        final File file = new File(folderName);
        file.mkdirs();
    }

    @Override
    public void printListFolderNameRoot() {
        System.out.println(PRINT_LIST);
        for (final String name : getListFolderNameRoot()) System.out.println(name);
    }

    @Override
    public @NotNull List<String> getListFolderNameRoot() {
        final File root = getRoot();
        final String[] directories = root.list((dir, name) -> new File(dir, name).isDirectory());
        if (directories == null) return Collections.emptyList();
        return Arrays.asList(directories);
    }

    private File getRoot() {
        return new File(settingService.getSyncFolder());
    }

    @Override
    public void createFolder(@Nullable String newFolderName) {
        if (newFolderName == null || newFolderName.isEmpty()) {
            int random = (int) (Math.random() * 10); //FIXME: Продумать другой алгоритм присваивания номера папки по умолчанию
            newFolderName = NEW_FOLDER_NAME + random;
        }
        String syncFolderName = settingService.getSyncFolder();
        final File file = new File(syncFolderName + newFolderName);
        file.mkdirs();
    }

    @Override
    public void deleteFolder(@Nullable final String folderName) {
        if (folderName == null || folderName.isEmpty()) return;
        final File file = new File(getRoot(), folderName);
        file.delete();
    }

    @Override
    public void clearRoot() {
        final File root = getRoot();
        final List<String> directories = getListFolderNameRoot();
        for (final String name : directories) {
            final File file = new File(root, name);
            file.delete();
        }
    }

}
