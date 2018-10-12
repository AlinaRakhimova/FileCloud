package ru.rakhimova.bean.local;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.rakhimova.local.FileLocalService;
import ru.rakhimova.system.SettingService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class FileLocalServiceBean { // implements FileLocalService { //FIXME

    private static final Logger LOGGER = Logger.getLogger(FileLocalServiceBean.class.getSimpleName());

    @Inject
    private SettingService settingService;

  //  @Override
    public void printListFileNameRoot() {
        for (String name : getListFileNameRoot()) System.out.println(name);
    }

  //  @Override
    public @NotNull List<String> getListFileNameRoot() {
        final File root = getRoot();
        final String[] directories = root.list((dir, name) -> new File(dir, name).isFile());
        return Arrays.asList(directories);
    }

    private File getRoot() {
        return new File(settingService.getSyncFolder());
    }


   // @Override
    public void clearRoot() {
        final  File root = getRoot();
        final List<String> files = getListFileNameRoot();
        for (final String name : files) {
            final File file = new File(root, name);
            file.delete();
        }
    }

    @Nullable
   // @Override
    @SneakyThrows
    public byte[] readData(@Nullable final String name) {
        if (name == null || name.isEmpty()) return new byte[]{};
        final File file = new File(getRoot(), name);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            return new byte[]{};
        }
    }

  //  @Override
    @SneakyThrows
    public void writeData(@Nullable final String name, byte[] data) {
        if (name == null || name.isEmpty()) return;
        final File file = new File(getRoot(), name);
        Path path = Paths.get(file.toURI());
        try {
            Files.write(path, data);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

   // @Override
    public boolean exist(@Nullable final String name) {
        if (name == null || name.isEmpty()) return false;
        final File file = new File(getRoot(), name);
        return file.exists();
    }

   // @Override
    public void deleteFile(@Nullable final String fileName) {
        if (fileName == null || fileName.isEmpty()) return;
        final File file = new File(getRoot(), fileName);
        file.delete();
    }

  //  @Override
    public void createTextFile(@Nullable String name, @Nullable String text) {
        if (text == null) return;
        writeData(name, text.getBytes());
    }
}
