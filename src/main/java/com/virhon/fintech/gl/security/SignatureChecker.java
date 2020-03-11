package com.virhon.fintech.gl.security;

import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.exception.AccessDenied;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.virhon.fintech.gl.api.APIConfig.DATE_HEADER_FORMAT;

/**
 * 1. Get the datetime from the header
 * 2. Validate the date
 * 3. Get client's uuid and the token from header
 * 4. Get stored (client's) digest=sha1(md5(apiKey.clientuuid.salt))
 * 5. Calculate the token as sha1(md5(datetime.data.digest.salt))
 * 6. Compare both tokens: provided by the header and calculated
 */
@Component
public class SignatureChecker {
    final static Logger LOGGER = Logger.getLogger(SignatureChecker.class);

    private static final String SALT = "unocolabatronix";

    @Autowired
    private APIConfig config;

    @Autowired
    private Authorizer authorizer;

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
    private static String encrypt(final String input) {
        return DigestUtils.sha1Hex(DigestUtils.md5Hex(input));
    }

    /**
     *
     * @param secretKey
     * @param clientUuid
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String calculateDigest(final String secretKey,
                                         final String clientUuid) {
        return encrypt(secretKey.concat(clientUuid.toLowerCase()).concat(SALT));
    }

    /**
     *
     * @param datetime
     * @param requestPayload
     * @param digest
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String calculateToken(final String datetime,
                                         final String requestPayload,
                                         final String digest) {
        return encrypt(datetime.concat(requestPayload).concat(digest).concat(SALT));
    }

    /**
     *
     * @param clientUuid
     * @param dateTime
     * @param requestPayload    - String presentation of request body
     * @param providedToken     - the token provided by client
     * @return
     */
    private boolean check(final String clientUuid,
                          final ZonedDateTime dateTime,
                          final String requestPayload,
                          final String providedToken) throws AccessDenied {
        final String clientDigest = authorizer.getDigest(clientUuid);
        final String dt = dateTime.format(DATE_HEADER_FORMAT);
        final String token = calculateToken(dt, requestPayload, clientDigest);
        return providedToken.equals(token);
    }

    public void validateSignature(final String clientUuid,
                                  final String strDt,
                                  final String requestPayload,
                                  final String providedToken) throws AuthenticationException {
        if (!config.isSignatureChecked()) {
            return;
        }
        try {
            //1. check date
            final ZonedDateTime dateTime = ZonedDateTime.parse(strDt, DATE_HEADER_FORMAT);
            if (!isValid(dateTime)) {
                LOGGER.error("Request expired");
                throw new AuthenticationException();
            }
            //2. check
            if (!check(clientUuid, dateTime, requestPayload, providedToken)) {
                LOGGER.error("Wrong signature");
                throw new AuthenticationException();
            }
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }
}