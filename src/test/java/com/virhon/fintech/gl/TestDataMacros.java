package com.virhon.fintech.gl;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

@Component
public class TestDataMacros {
    private Map<String, String> accounts = new HashMap<>();

    @PostConstruct
    public void init() throws IOException {
        InputStreamReader isReader=
                new InputStreamReader(new FileInputStream(new File("macros.lst")));
        BufferedReader br = new BufferedReader(isReader);
        final String brLine = br.readLine();
        if (brLine != null) {
            final String line = brLine
                    .replace(" ", "")
                    .replace("{", "")
                    .replace("}", "");
            Arrays.asList(line.split(",")).forEach(s -> {
                final List<String> vals = new ArrayList<>();
                Arrays.asList(s.split("=")).forEach(ss -> vals.add(ss));
                final String key =
                        accounts.put(vals.get(0), vals.get(1));
            });
        }
    }

    public String getObjectUuid(String macro){
        final String res = this.accounts.get(macro);
        return res;
    }
}
