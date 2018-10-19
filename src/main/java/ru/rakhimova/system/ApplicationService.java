package ru.rakhimova.system;

import org.jetbrains.annotations.Nullable;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;

public interface ApplicationService {

    void init();

    void shutdown();

    boolean login();

    boolean logout();

    boolean status();

    boolean save();

    @Nullable
    Exception error();

    @Nullable
    Repository repository();

    @Nullable
    Session session();

    @Nullable
    Node getRootNode();

}
