package ru.rakhimova;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.rakhimova.system.SettingService;

import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.net.MalformedURLException;


//@RunWith(CdiTestRunner.class)
public class AppTest {

    private static final String URL = "http://localhost:8080/rmi";

//    @Inject
//    private SettingService settingService;

    @Test
    public void loginTest() throws MalformedURLException, RepositoryException {

        final Repository repository = new URLRemoteRepository(URL);
        final Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        session.logout();
    }

}
