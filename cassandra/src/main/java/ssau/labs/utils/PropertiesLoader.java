package ssau.labs.utils;

import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {
    private static PropertiesLoader loader;
    private final Properties properties;

    private PropertiesLoader() throws IOException {
        properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("db.properties"));
    }

    static public PropertiesLoader getInstance() throws IOException {
        if (loader == null) {
            loader = new PropertiesLoader();
        }
        return loader;
    }

    public Properties getProperties() {
        return properties;
    }
}
