package ru.rakhimova.bean.service;

import lombok.SneakyThrows;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.jboss.weld.environment.se.WeldContainer;
import org.jetbrains.annotations.Nullable;
import ru.rakhimova.annotation.Loggable;
import ru.rakhimova.system.ApplicationService;
import ru.rakhimova.system.SettingService;
import ru.rakhimova.system.TimerService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

@ApplicationScoped
public class ApplicationServiceBean implements ApplicationService {

    @Inject
    private SettingService settingService;

    @Inject
    private TimerService timerService;

    @Inject
    private WeldContainer container;

    private Repository repository = null;

    private Session session = null;

    private Exception error = null;

    @Loggable
    public void init() {
        if (settingService.getJcrActive()) login();
        if (settingService.getSyncActive()) timerService.start();
    }

    @Override
    @Loggable
    public boolean login() {
        if (status()) return false;
        try {
            final String jcrURL = settingService.getJcrUrl();
            repository = new URLRemoteRepository(jcrURL);
            final String jcrLogin = settingService.getJcrLogin();
            final String jcrPassword = settingService.getJcrPassword();
            final char[] password = jcrPassword.toCharArray();
            final SimpleCredentials credentials = new SimpleCredentials(jcrLogin, password);
            session = repository.login(credentials);
            return true;
        } catch (final Exception e) {
            error = e;
            return false;
        }
    }

    @Override
    @Loggable
    public boolean logout() {
        if (repository == null) return false;
        if (session == null) return false;
        try {
            session.logout();
            repository = null;
            session = null;
            return true;
        } catch (Exception e) {
            error = e;
            return false;
        }
    }

    @Override
    public boolean status() {
        return repository != null && session != null;
    }

    @Override
    @Nullable
    public Exception error() {
        return error;
    }

    @Override
    @Nullable
    public Repository repository() {
        return repository;
    }

    @Override
    @Nullable
    public Session session() {
        return session;
    }

    @Override
    @Nullable
    @SneakyThrows
    public Node getRootNode() {
        if (!status()) return null;
        return session.getRootNode();
    }

    @Override
    public void shutdown() {
        container.shutdown();
        System.exit(0);
    }

    @Override
    @SneakyThrows
    public boolean save() {
        if (!status()) return false;
        session.save();
        return true;
    }
}
