package ru.rakhimova.bean.service;

import ru.rakhimova.annotation.Loggable;
import ru.rakhimova.system.SettingService;
import ru.rakhimova.system.TimerService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Timer;

@ApplicationScoped
public class TimerServiceBean implements TimerService {

    @Inject
    private SettingService settingService;

    private final Timer timer = new Timer();

    private SyncTaskBean task = null;

    @Override
    public boolean getActive() {
        return task != null;
    }

    @Override
    public void setActive(boolean active) {
        if (active) start();
        else stop();
    }

    @Loggable
    @Override
    public synchronized boolean start() {
        if (task != null) return false;
        final Integer timeout = settingService.getSyncTimeout();
        SyncTaskBean task = new SyncTaskBean();
        timer.schedule(task.get(), 0, timeout);
        return true;
    }

    @Loggable
    @Override
    public boolean stop() {
        if (task == null) return false;
        task.cancel();
        task = null;
        return true;
    }

    @Override
    public void restart() {
        stop();
        start();
    }
}
