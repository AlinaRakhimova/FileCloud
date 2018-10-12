package ru.rakhimova.bean.service;

import ru.rakhimova.annotation.Loggable;
import ru.rakhimova.bean.local.FolderLocalServiceBean;
import ru.rakhimova.local.FolderLocalService;
import ru.rakhimova.system.ApplicationService;
import ru.rakhimova.system.BootstrapService;
import ru.rakhimova.system.SettingService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

@ApplicationScoped
public class BootstrapServiceBean implements BootstrapService {

    @Inject
    private ApplicationService applicationService;

    @Inject
    private SettingService settingService;

//    @Inject
//    private FolderLocalService folderLocalService;

    private FolderLocalServiceBean folderLocalService = CDI.current().select(FolderLocalServiceBean.class).get(); //FIXME

    @Loggable
    public void init() {
        settingService.init();
        folderLocalService.init();
        applicationService.init();
    }
}
