package ru.rakhimova.bean.service;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.rakhimova.annotation.Loggable;
import ru.rakhimova.system.SettingService;

import javax.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.util.Properties;

@Getter
@Setter
@ApplicationScoped
public class SettingServiceBean implements SettingService {

    private static final String FILE_NAME = "application.properties";
    private static final String KEY_JCR_URL = "jcr.url";
    private static final String KEY_JCR_LOGIN = "jcr.login";
    private static final String KEY_JCR_PASSWORD = "jcr.password";
    private static final String KEY_JCR_ACTIVE = "jcr.active";

    private static final String KEY_SYNC_ACTIVE = "sync.active";
    private static final String KEY_SYNC_FOLDER = "sync.folder";
    private static final String KEY_SYNC_TIMEOUT = "sync.timeout";

    private static final String JCR_URL_DEFAULT = "localhost";
    private static final String JCR_LOGIN_DEFAULT = "admin";
    private static final String JSC = "admin";
    private static final String SYNC_FOLDER_DEFAULT = "./temp/";

    private String jcrLogin;
    private String jcrPassword;
    private String jcrUrl;
    private boolean jcrActive;
    private String syncFolder;
    private boolean syncActive;
    private Integer syncTimeout;

    @Loggable
    @SneakyThrows
    public void init() {
        final Properties properties = new Properties();
        final ClassLoader classLoader = SettingServiceBean.class.getClassLoader();
        final InputStream inputStream = classLoader.getResourceAsStream(FILE_NAME);
        properties.load(inputStream);

        jcrUrl = properties.getOrDefault(KEY_JCR_URL, JCR_URL_DEFAULT).toString();
        jcrLogin = properties.getOrDefault(KEY_JCR_LOGIN, JCR_LOGIN_DEFAULT).toString();
        jcrPassword = properties.getOrDefault(KEY_JCR_PASSWORD, JSC).toString();
        jcrActive = Boolean.parseBoolean(properties.getOrDefault(KEY_JCR_ACTIVE, true).toString());
        syncFolder = properties.getOrDefault(KEY_SYNC_FOLDER, SYNC_FOLDER_DEFAULT).toString();
        syncTimeout = Integer.parseInt(properties.getOrDefault(KEY_SYNC_TIMEOUT, 1000).toString());
        syncActive = Boolean.parseBoolean(properties.getOrDefault(KEY_SYNC_ACTIVE, false).toString());
    }

    @Override
    public boolean getJcrActive() {
        return jcrActive;
    }

    @Override
    @NotNull
    public String getJcrUrl() {
        return jcrUrl;
    }

    @Override
    @NotNull
    public String getJcrLogin() {
        return jcrLogin;
    }

    @Override
    @NotNull
    public String getJcrPassword() {
        return jcrPassword;
    }

    @Override
    @NotNull
    public String getSyncFolder() {
        return syncFolder;
    }

    @Override
    @NotNull
    public Integer getSyncTimeout() {
        return syncTimeout;
    }

    @Override
    public boolean getSyncActive() {
        return syncActive;
    }

}
