package ru.rakhimova.system;

import org.jetbrains.annotations.NotNull;

public interface SettingService {

    void init();

    @NotNull
    boolean getJcrActive();

    @NotNull
    String getJcrUrl();

    @NotNull
    String getJcrLogin();

    @NotNull
    String getJcrPassword();

    @NotNull
    String getSyncFolder();

    @NotNull
    Integer getSyncTimeout();

    @NotNull
    boolean getSyncActive();

}
