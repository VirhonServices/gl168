package com.virhon.fintech.gl.auth;

import com.virhon.fintech.gl.api.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest(classes = Application.class)
public class AuthenticatorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private Authenticator auth;

    @Test
    public void testDoAuth() {

    }
}