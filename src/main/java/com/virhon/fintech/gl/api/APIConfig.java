package com.virhon.fintech.gl.api;

import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Configuration
public class APIConfig {
    public static final String PROPERTIES_FILE_NAME = "/application.properties";
    public static final String SECURE_WINDOW = "secure-window";
    public static final String CLIENTS_LIST = "digests-list";
    public static final DateTimeFormatter DATE_HEADER_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    public static final String CLIENT_UUID_HEADER = "x-client-uuid";
    public static final String SIGNATURE_HEADER = "x-signature";
    public static final String DATE_HEADER = "date";

    private boolean isSignatureChecked = false;
    private final Properties properties = new Properties();

    public APIConfig() throws IOException {
        InputStream input = this.getClass().getResourceAsStream(PROPERTIES_FILE_NAME);
        properties.load(input);
    }

    public final String getProperty(final String propertyName) {
        return properties.getProperty(propertyName);
    }

    public Integer getSecureWindow() {
        final String result = getProperty(SECURE_WINDOW);
        return Integer.valueOf(result);
    }

    public String getClientsListFilename() {
        return getProperty(CLIENTS_LIST);
    }

    public void setSignatureChecked(boolean signatureChecked) {
        this.isSignatureChecked = signatureChecked;
    }

    public boolean isSignatureChecked() {
        return isSignatureChecked;
    }
}