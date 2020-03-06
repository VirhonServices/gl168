package com.virhon.fintech.gl.signature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;

import static org.testng.Assert.*;

@SpringBootTest
public class SignatureCheckerTest {

    @Autowired
    private SignatureChecker signator;

    @Test(enabled = true)
    public void createDigests() {
        final List<String> keys = new ArrayList<>();
        final Map<String, String> digests = new HashMap<>();
        for (int i=0; i<10; i++) {
            final String key = UUID.randomUUID().toString().toUpperCase();
            keys.add(key);
            final String clientUuid = UUID.randomUUID().toString();
            final String digest = SignatureChecker.calculateDigest(key, clientUuid);
            digests.put(clientUuid, digest);
        }
        System.out.println("=========== SECRET KEYS =============");
        keys.forEach(k -> System.out.println(k));
        System.out.println("============= DIGESTS ===============");
        digests.forEach((c,d) -> System.out.println(c.concat("=").concat(d)));
    }

    @Test
    public void testValidateSignature() {
        final String clientUuid = "9a0fd125-2e7e-486c-8884-97e4275adf90";
        final String digest = "53b179afe1b7e001b3e881a31e0ddee7c2063f71";
        final String date = LocalDate.now().toString();
        final String data = "{emptyData}";
        final String token = SignatureChecker.calculateToken(date, data, digest);
        Assert.assertEquals(token, "bcb6aef0dbbeab82692c0db18df96ff1423ee5ec");
    }
}