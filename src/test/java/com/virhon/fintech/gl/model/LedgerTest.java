package com.virhon.fintech.gl.model;

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
    private Account account1;
    private Account account2;
    private Account account3;
    private Account account4;

    public LedgerTest() throws IOException {
    }

    @Test(enabled = false)
    public void createAccounts() throws IOException {
        this.account1 = uahLedger.openNew("1001200048475", "UA673052991001200048475",
                AccountType.ACTIVE);
        this.account2 = uahLedger.openNew("2600100003894", "UA673052992600100003894",
                AccountType.PASSIVE);
        this.account3 = uahLedger.openNew("1002900013423", "UA223052991002900013423",
                AccountType.ACTIVE);
        this.account4 = uahLedger.openNew("2602100009203", "UA673052992602100009203",
                AccountType.PASSIVE);
        MySQLStorageConnection.getInstance().commit();
    }

    @Test(priority = 1)
    public void testTransferFunds() throws LedgerException, IOException {
        final BigDecimal amount = new BigDecimal("100.00");
        final Account debit = uahLedger.getExistingByIban("UA673052991001200048475");
        final BigDecimal debitBalBefore = debit.getAttributes().getEntity().getBalance();
        final Account credit = uahLedger.getExistingByIban("UA673052992600100003894");
        final BigDecimal creditBalBefore = credit.getAttributes().getEntity().getBalance();
        final IdentifiedEntity<Transfer> tr = uahLedger.transferFunds("TEST-REF-1", debit.getAccountId(),
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
        final Account debit = uahLedger.getExistingByIban("UA673052991001200048475");
        final BigDecimal debitBalBefore = debit.getAttributes().getEntity().getBalance();
        final Account credit = uahLedger.getExistingByIban("UA673052992600100003894");
        final BigDecimal creditBalBefore = credit.getAttributes().getEntity().getBalance();
        final IdentifiedEntity<Transfer> tr = uahLedger.transferFunds("TEST-REF-1", credit.getAccountId(),
                debit.getAccountId(), amount, amount,
                LocalDate.now(), "Testing transfer");
    }

    @Test(priority = 3, expectedExceptions = LedgerException.class)
    public void testReservation() throws LedgerException {
        final Account debit  = uahLedger.getExistingByIban("UA673052992600100003894");
        final Account credit = uahLedger.getExistingByIban("UA673052991001200048475");
        final BigDecimal balance = debit.getAttributes().getEntity().getBalance();
        final BigDecimal successAmount = balance.multiply(new BigDecimal("0.5")).negate();
        uahLedger.reserveFunds("RESERVATION_REF1",debit.getAccountId(), credit.getAccountId(), successAmount,
                "reservation-success");
        uahLedger.reserveFunds("RESERVATION_REF2",debit.getAccountId(), credit.getAccountId(),
                balance.negate(),"reservation-failed");
    }

    @Test(priority = 4)
    public void testCancelReservation() throws LedgerException {
        final List<IdentifiedEntity<Reservation>> res = new ArrayList<>();
        final Account debit  = uahLedger.getExistingByIban("UA673052992600100003894");
        final Account credit = uahLedger.getExistingByIban("UA673052991001200048475");
        final BigDecimal balance = debit.getAttributes().getEntity().getBalance();
        for (Integer i=0;i<10;i++) {
            final BigDecimal amount = new BigDecimal(i.toString());
            res.add(uahLedger.reserveFunds("RESREF#".concat(i.toString()),debit.getAccountId(),
                    credit.getAccountId(), amount,"reservation-test"));
        }
        final Long id = res.get(3).getId();
        uahLedger.cancelReservation(id);
        final IdentifiedEntity<Reservation> r = uahLedger.getReservationRepo().getById(id);
        Assert.assertNull(r);
    }
}