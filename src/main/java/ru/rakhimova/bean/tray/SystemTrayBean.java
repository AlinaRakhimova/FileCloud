package ru.rakhimova.bean.tray;

import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.bean.service.SyncServiceBean;
import ru.rakhimova.system.SystemTrayInterface;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static java.awt.SystemTray.getSystemTray;

@ApplicationScoped
public class SystemTrayBean implements SystemTrayInterface {

    private static final String MESSAGE = "Data synchronization is in progress.";
    private static final String TITLE = "Synchronization";
    private static final String TRAY_ICON_PNG = ".\\src\\main\\resources\\image\\trayIcon.png";

    private SyncServiceBean syncService = new SyncServiceBean();
    private SystemTray tray;
    private TrayIcon trayIcon;

    @Override
    @SneakyThrows
    public void addAppToTray(@NotNull final Stage primaryStage) {
        Toolkit.getDefaultToolkit();

        if (!SystemTray.isSupported()) {
            System.out.println("No system tray support, application exiting.");
            Platform.exit();
        }

        tray = getSystemTray();
        final Image image = ImageIO.read(new File(TRAY_ICON_PNG));
        trayIcon = new TrayIcon(image);

        final MenuItem openItem = new MenuItem("Open application"); //FIXME: Открытие клиентского интерфейса производится только первый раз
        openItem.addActionListener((ActionEvent event) -> Platform.runLater(() -> showStage(primaryStage)));

        final MenuItem syncItem = new MenuItem("Synchronize");
        syncItem.addActionListener(event -> {
            Platform.runLater(() -> syncService.sync());
            trayIcon.displayMessage(TITLE, MESSAGE, TrayIcon.MessageType.INFO);
        });

        final MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(event -> {
            Platform.exit();
            tray.remove(trayIcon);
        });

        final PopupMenu popup = new PopupMenu();
        popup.add(openItem);
        popup.addSeparator();
        popup.add(syncItem);
        popup.addSeparator();
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);
        tray.add(trayIcon);
    }

    private void showStage(@NotNull final Stage primaryStage) {
        primaryStage.toFront();
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

}