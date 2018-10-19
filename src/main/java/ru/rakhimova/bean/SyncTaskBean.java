package ru.rakhimova.bean;

import ru.rakhimova.system.SyncService;
import ru.rakhimova.system.SyncTask;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.TimerTask;

@ApplicationScoped
public class SyncTaskBean extends TimerTask implements SyncTask {

//    @Inject
//    private SyncService syncService;

    public TimerTask get() {
        return this;
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    public void run() {
        //syncService.sync();
    }
}
