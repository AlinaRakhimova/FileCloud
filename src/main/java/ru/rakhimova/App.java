package ru.rakhimova;

import ru.rakhimova.GUI.ClientGUI;
import ru.rakhimova.bean.service.BootstrapServiceBean;

import javax.enterprise.inject.se.SeContainerInitializer;

public class App {

    private static ClientGUI clientUI = new ClientGUI();

    public static void main(String[] args) {
        SeContainerInitializer.newInstance().addPackages(App.class).initialize().select(BootstrapServiceBean.class).get().init();
        clientUI.startUI();
    }

}