package ru.rakhimova.bean.service;

import ru.rakhimova.annotation.Loggable;
import ru.rakhimova.system.SyncTask;

import javax.enterprise.context.ApplicationScoped;
import java.util.TimerTask;

@ApplicationScoped
public class SyncTaskBean extends TimerTask implements SyncTask {

    private SyncServiceBean syncService = new SyncServiceBean();

    public TimerTask get() {
        return this;
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    @Loggable
    public void run() {
        syncService.sync();
    }
}
