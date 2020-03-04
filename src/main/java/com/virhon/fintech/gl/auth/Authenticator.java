package com.virhon.fintech.gl.auth;

import com.virhon.fintech.gl.api.APIConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.naming.AuthenticationException;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 1. Get the datetime from the header
 * 2. Validate the date
 * 3. Get client's uuid and the token from header
 * 4. Get stored (client's) digest=sha1(md5(apiKey.clientuuid.salt))
 * 5. Calculate the token as sha1(md5(datetime.data.digest.salt))
 * 6. Compare both tokens: provided by the header and calculated
 */
@Component
public class Authenticator {
    private static final String SALT = "unocolabatronix";
    private Map<String, String> digests = new HashMap<>();

    @Autowired
    private APIConfig config;

    @PostConstruct
    public void init() throws IOException {
        loadDigests();
    }

    private void loadDigests() throws IOException {
        final String filename = config.getAuthFilename();
        InputStreamReader isReader=
                new InputStreamReader(new FileInputStream(new File(filename)));
        BufferedReader br = new BufferedReader(isReader);
        String brLine = br.readLine();
        while (brLine != null) {
            final List<String> pair = new ArrayList<>();
            Arrays.asList(brLine.trim().split("=")).forEach(kp -> pair.add(kp));
            digests.put(pair.get(0), pair.get(1));
            brLine = br.readLine();
        }
    }

    private boolean isValid(final ZonedDateTime dateTime) {
        final ZonedDateTime begin = ZonedDateTime.now();
        return ChronoUnit.SECONDS.between(dateTime, begin) < config.getSecureWindow();
    }

    /**
     *
     * @param input
     * @return SHA1(MD5(input))
     * @throws NoSuchAlgorithmException
     */
    private static byte[] encrypt(final String input) throws NoSuchAlgorithmException {
        final MessageDigest md1 = MessageDigest.getInstance("SHA-1");
        final MessageDigest md5 = MessageDigest.getInstance("MD5");
        final byte[] md5msg = md5.digest(input.getBytes());
        final byte[] md1msg = md1.digest(md5msg);
        return md1msg;
    }

    /**
     *
     * @param datetime
     * @param requestPayload
     * @param digest
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static String calculateToken(final String datetime,
                                         final String requestPayload,
                                         final String digest) throws NoSuchAlgorithmException {
        return new String(encrypt(datetime.concat(requestPayload).concat(digest).concat(SALT)));
    }

    /**
     *
     * @param clientUuid
     * @param dateTime
     * @param requestPayload    - String presentation of request body
     * @param providedToken     - the token provided by client
     * @return
     */
    public boolean doAuth(final String clientUuid,
                          final ZonedDateTime dateTime,
                          final String requestPayload,
                          final String providedToken) throws NoSuchAlgorithmException, AuthenticationException {
        if (!config.isAuthOn()) {
            return true;
        }
        if (!isValid(dateTime)) {
            throw new AuthenticationException();
        }
        final String clientDigest = this.digests.get(clientUuid);
        final String dt = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss"));
        final String token = calculateToken(dt, requestPayload, clientDigest);
        return providedToken.equals(token);
    }
}
