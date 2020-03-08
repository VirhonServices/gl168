package com.virhon.fintech.gl.security;

import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.exception.AccessDenied;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

@Component
public class Authorizer {
    private Map<String, String> digests = new HashMap<>();
    private List<Pair<String, String>> clients = new ArrayList<>();

    @Autowired
    private APIConfig config;

    @PostConstruct
    public void init() throws IOException {
        loadDigests();
    }

    private void loadDigests() throws IOException {
        final String filename = config.getClientsListFilename();
        InputStreamReader isReader=
                new InputStreamReader(new FileInputStream(new File(filename)));
        BufferedReader br = new BufferedReader(isReader);
        String brLine = br.readLine();
        while (brLine != null) {
            final List<String> pair = new ArrayList<>();
            Arrays.asList(brLine.trim().split("=")).forEach(kp -> pair.add(kp));
            final Pair<String, String> p = new Pair<>(pair.get(0), pair.get(1));
            digests.put(p.getKey(), p.getValue());
            clients.add(p);
            brLine = br.readLine();
        }
    }

    public void checkPresense(final String clientUuid) throws AccessDenied {
        if (!isPresent(clientUuid)) {
            throw new AccessDenied();
        }
    }

    public boolean isPresent(final String clientUuid) {
        return this.digests.keySet().contains(clientUuid);
    }

    public String getDigest(final String clientUuid) throws AccessDenied {
        checkPresense(clientUuid);
        return this.digests.get(clientUuid);
    }

    public List<Pair<String, String>> getClients() {
        return this.clients;
    }

    public Pair<String, String> get(int index) {
        return this.clients.get(index);
    }



}
