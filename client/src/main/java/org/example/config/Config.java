package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    public static final String HOST;
    public static final int PORT;

    static {
        try (InputStream input = Config.class.getResourceAsStream("/application.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            HOST = properties.getProperty("host");
            PORT = Integer.parseInt(properties.getProperty("port"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}