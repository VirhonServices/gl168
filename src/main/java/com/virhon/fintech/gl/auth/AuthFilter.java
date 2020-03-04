package com.virhon.fintech.gl.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.naming.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component
public class AuthFilter extends GenericFilterBean {

    @Autowired
    private Authenticator authenticator;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)(request);
        HttpServletResponse httpResponse = (HttpServletResponse)(response);
        Optional<String> oClient = Optional.of(httpRequest.getHeader("X-Auth-User"));
        Optional<String> oToken = Optional.of(httpRequest.getHeader("X-Auth-Token"));
        Optional<String> oDate = Optional.of(httpRequest.getHeader("Date"));
        final String body = new String(HttpHelper.getBodyString(request).getBytes(Charset.forName("UTF-8")));
        if (!oDate.isPresent()) {
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        if (!oClient.isPresent()) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        if (!oToken.isPresent()) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
        try {
            authenticator.doAuth(oClient.get(), ZonedDateTime.parse(oDate.get()), body, oToken.get());
            chain.doFilter(request, response);
        } catch (AuthenticationException e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void destroy() {

    }
}
