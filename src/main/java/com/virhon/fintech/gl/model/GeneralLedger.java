package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.repo.LedgerRepoFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralLedger {
    private Map<String, Ledger> ledgers = new HashMap<>();

    public GeneralLedger(LedgerRepoFactory factory, List<String> currencies) throws IOException {
        for (int i=0;i<currencies.size();i++) {
            final String c = currencies.get(i);
            ledgers.put(c, new Ledger(factory));
        }
    }
}
