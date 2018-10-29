package ru.rakhimova.bean.local;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rakhimova.annotation.Loggable;
import ru.rakhimova.local.FolderLocalService;
import ru.rakhimova.system.SettingService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class FolderLocalServiceBean implements FolderLocalService {

    private static final String NEW_FOLDER_NAME = "New folder ";

    private static final String PRINT_LIST = "List local folders:";

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
        for (final File name : getListFolderNameRoot()) System.out.println(name.toString());
    }

    @Override
    public @NotNull List<File> getListFolderNameRoot() {
        final File root = getRoot();
        File[] arrayFolders = root.listFiles(File::isDirectory);
        if (arrayFolders != null) {
            return new ArrayList<>(Arrays.asList(arrayFolders));
        } else return Collections.emptyList();
    }

    public File getRoot() {
        return new File(settingService.getSyncFolder());
    }

    @Override
    public void createFolder(@Nullable String newFolderName) {
        if (newFolderName == null || newFolderName.isEmpty()) {
            int random = (int) (Math.random() * 10); //FIXME: Продумать другой алгоритм присваивания номера папки по умолчанию
            newFolderName = NEW_FOLDER_NAME + random;
        }
        String syncFolderName = settingService.getSyncFolder();
        String path = syncFolderName + newFolderName;
        final File file = new File(path);
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
        File[] directories = root.listFiles();
        if (directories != null) {
            for (File file : directories) {
                file.delete();
            }
        }
    }

}
