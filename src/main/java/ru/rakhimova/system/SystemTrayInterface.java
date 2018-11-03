package ru.rakhimova.system;

import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public interface SystemTrayInterface {

    void addAppToTray(@NotNull Stage primaryStage);

}
