package com.virhon.fintech.gl;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

@Component
public class TestDataMacros {
    private Map<String, String> accounts = new HashMap<>();

    @PostConstruct
    void init() throws IOException {
        InputStreamReader isReader=
                new InputStreamReader(new FileInputStream(new File("macros.lst")));
        BufferedReader br = new BufferedReader(isReader);
        final String line = br.readLine();
        Arrays.asList(line.split(",")).forEach(s -> {
            final List<String> vals = new ArrayList<>();
            Arrays.asList(s.split("=")).forEach(ss -> vals.add(ss));
            accounts.put(vals.get(0), vals.get(1));
        });
    }

    public String getObjectUuid(String macro){
        return accounts.get(macro);
    }
}
