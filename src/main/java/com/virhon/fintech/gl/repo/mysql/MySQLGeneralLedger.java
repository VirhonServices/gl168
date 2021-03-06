package com.virhon.fintech.gl.repo.mysql;

import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.GeneralLedger;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.repo.LedgerRepoFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MySQLGeneralLedger implements GeneralLedger {
    private Map<String, Ledger> ledgers = new HashMap<>();

    @PostConstruct
    public void init() throws IOException {
        final List<String> currencies = new ArrayList<>();
        // TODO: 20.02.20 read from properties
        currencies.add("UAH");
        currencies.add("USD");
        currencies.add("EUR");
        for (int i=0;i<currencies.size();i++) {
            final String c = currencies.get(i);
            final LedgerRepoFactory factory = new MySQLLedgerRepoFactory(c);
            ledgers.put(c, new Ledger(factory));
        }
    }

    @Override
    public Ledger getLedger(String currency) throws LedgerException {
        final String cur = currency.toUpperCase();
        if (!this.ledgers.keySet().contains(cur)) {
            throw LedgerException.notSupportedCurrency(currency);
        }
        return ledgers.get(cur);
    }

    @Override
    public void commit() throws IOException {
        MySQLStorageConnection.getInstance().commit();
    }
}
