package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;

import java.io.IOException;

public interface GeneralLedger {
    Ledger getLedger(String currency) throws LedgerException;
    void commit() throws IOException;
}
