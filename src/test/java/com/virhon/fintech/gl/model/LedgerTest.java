package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.LedgerRepoFactory;
import com.virhon.fintech.gl.repo.mysql.MySQLLedgerRepoFactory;
import com.virhon.fintech.gl.repo.mysql.MySQLStorageConnection;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class LedgerTest {
    private LedgerRepoFactory uahFactory = new MySQLLedgerRepoFactory("UAH");
    private Ledger uahLedger = new Ledger(uahFactory);
    private TestDataMacros macros = new TestDataMacros();

    public LedgerTest() throws IOException {
        macros.init();
    }

    @Test(priority = 1)
    public void testTransferFunds() throws LedgerException, IOException {
        final BigDecimal amount = new BigDecimal("100.00");
        final Account debit = uahLedger.getExistingByUuid(macros.getObjectUuid("ACTIVE2"));
        final BigDecimal debitBalBefore = debit.getAttributes().getEntity().getBalance();
        final Account credit = uahLedger.getExistingByUuid(macros.getObjectUuid("PASSIVE_EMPTY2"));
        final BigDecimal creditBalBefore = credit.getAttributes().getEntity().getBalance();
        final Transfer tr = uahLedger.transferFunds("TEST-REF-1", "CLIENT_UUID", "ClientCustomerId", debit.getAccountId(),
                credit.getAccountId(), amount, amount,
                LocalDate.now(), "Testing transfer");
        MySQLStorageConnection.getInstance().commit();
        final BigDecimal debitBalAfter = debit.getAttributes().getEntity().getBalance();
        final BigDecimal creditBalAfter = credit.getAttributes().getEntity().getBalance();
        Assert.assertEquals(debitBalAfter.subtract(debitBalBefore), amount);
        Assert.assertEquals(creditBalAfter.subtract(creditBalBefore), amount.negate());
    }

    @Test(priority = 2, expectedExceptions = LedgerException.class)
    public void testRedSaldo() throws LedgerException {
        final BigDecimal amount = new BigDecimal("10000.00");
        final Account debit = uahLedger.getExistingByUuid(macros.getObjectUuid("ACTIVE2"));
        final BigDecimal debitBalBefore = debit.getAttributes().getEntity().getBalance();
        final Account credit = uahLedger.getExistingByUuid(macros.getObjectUuid("PASSIVE_EMPTY2"));
        final BigDecimal creditBalBefore = credit.getAttributes().getEntity().getBalance();
        final Transfer tr = uahLedger.transferFunds("TEST-REF-1", "CLIENT_UUID", "ClientCustomerId", credit.getAccountId(),
                debit.getAccountId(), amount, amount,
                LocalDate.now(), "Testing transfer");
    }

    @Test(priority = 3, expectedExceptions = LedgerException.class)
    public void testReservation() throws LedgerException {
        final Account debit = uahLedger.getExistingByUuid(macros.getObjectUuid("PASSIVE_EMPTY2"));
        final Account credit = uahLedger.getExistingByUuid(macros.getObjectUuid("ACTIVE2"));
        final BigDecimal balance = debit.getAttributes().getEntity().getBalance();
        final BigDecimal successAmount = balance.multiply(new BigDecimal("0.5")).negate();
        uahLedger.reserveFunds("RESERVATION_REF1","CLIENT_UUID", "ClientCustomerId",
                debit.getAccountId(), credit.getAccountId(), successAmount,"reservation-success");
        uahLedger.reserveFunds("RESERVATION_REF2","CLIENT_UUID", "ClientCustomerId",
                debit.getAccountId(), credit.getAccountId(), balance.negate(),"reservation-failed");
    }

    @Test(priority = 4)
    public void testCancelReservation() throws LedgerException {
        final List<IdentifiedEntity<Reservation>> res = new ArrayList<>();
        final Account debit = uahLedger.getExistingByUuid(macros.getObjectUuid("PASSIVE_EMPTY2"));
        final Account credit = uahLedger.getExistingByUuid(macros.getObjectUuid("ACTIVE2"));
        final BigDecimal balance = debit.getAttributes().getEntity().getBalance();
        for (Integer i=0;i<10;i++) {
            final BigDecimal amount = new BigDecimal(i.toString());
            res.add(uahLedger.reserveFunds("RESREF#".concat(i.toString()), "CLIENT_UUID",
                    "ClientCustomerId", debit.getAccountId(),credit.getAccountId(), amount,
                    "reservation-test"));
        }
        final Long id = res.get(3).getId();
        uahLedger.cancelReservation(id);
        final IdentifiedEntity<Reservation> r = uahLedger.getReservationRepo().getById(id);
        Assert.assertNull(r);
    }

}