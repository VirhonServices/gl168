package com.virhon.fintech.gl.api;

import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class APIConfig {
    public static final String PROPERTIES_FILE_NAME = "/application.properties";
    private final Properties properties = new Properties();

    public APIConfig() throws IOException {
        InputStream input = this.getClass().getResourceAsStream(PROPERTIES_FILE_NAME);
        properties.load(input);
    }

    public final String getProperty(final String propertyName) {
        return properties.getProperty(propertyName);
    }
}