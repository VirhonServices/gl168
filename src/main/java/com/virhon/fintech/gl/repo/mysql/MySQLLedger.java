package com.virhon.fintech.gl.repo.mysql;

import com.virhon.fintech.gl.repo.*;
import com.virhon.fintech.gl.repo.mysql.accountattribute.MySQLAttrRepo;
import com.virhon.fintech.gl.repo.mysql.currentpage.MySQLCurrentPageRepo;
import com.virhon.fintech.gl.repo.mysql.historicalpage.MySQLHistoricalPageRepo;
import com.virhon.fintech.gl.repo.mysql.reservation.MySQLReservationRepo;
import com.virhon.fintech.gl.repo.mysql.transfer.MySQLTransferRepo;

import java.io.IOException;

public class MySQLLedger implements LedgerFactory {
    public static final String ACCOUNT_ATTRIBUTE = "account_attribute";
    public static final String CURRENT_PAGE = "current_page";
    public static final String HISTORICAL_PAGE = "historical_page";
    public static final String RESERVATION = "reservation";
    public static final String TRANSFER = "transfer";
    private String currency;

    public MySQLLedger(String currency) {
        this.currency = currency;
    }

    private String getCurrencyTableName(String tablename) {
        return currency.toLowerCase().concat("_").concat(tablename);
    }

    @Override
    public AttrRepo getAccountAttributeRepository() throws IOException {
        return new MySQLAttrRepo(getCurrencyTableName(ACCOUNT_ATTRIBUTE));
    }

    @Override
    public CurPageRepo getCurrentPageRepository() throws IOException {
        return new MySQLCurrentPageRepo(getCurrencyTableName(CURRENT_PAGE));
    }

    @Override
    public HistPageRepo getHistoricalPageRepository() throws IOException {
        return new MySQLHistoricalPageRepo(getCurrencyTableName(HISTORICAL_PAGE));
    }

    @Override
    public ReservationRepo getReservationRepository() throws IOException {
        return new MySQLReservationRepo(getCurrencyTableName(RESERVATION));
    }

    @Override
    public TransferRepo getTransferRepository() throws IOException {
        return new MySQLTransferRepo(getCurrencyTableName(TRANSFER));
    }

    public String getCurrency() {
        return currency;
    }
}
