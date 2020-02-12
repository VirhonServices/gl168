package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.mysql.accountattribute.MySQLAttrRepo;
import com.virhon.fintech.gl.repo.mysql.currentpage.MySQLCurrentPageRepo;
import com.virhon.fintech.gl.repo.mysql.historicalpage.MySQLHistoricalPageRepo;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountTest {
    private MySQLAttrRepo               attrRepo = new MySQLAttrRepo();
    private MySQLCurrentPageRepo        cRepo = new MySQLCurrentPageRepo();
    private MySQLHistoricalPageRepo     hRepo = new MySQLHistoricalPageRepo();

    public AccountTest() throws IOException {
    }

    @Test(enabled = false)
    void testCreating() throws LedgerException {
        final List<Long> accountIds = new ArrayList<>();
        // 1. Create new accounts
        for (long i=0;i<4;i++) {
            final String accountNumber = "260010987654321".concat(Long.valueOf(1562L + (new Random().nextInt(1000))).toString());
            final String iban = "UA56305299".concat(accountNumber);
            final Account account = Account.openNew(accountNumber, iban, AccountType.PASSIVE, attrRepo, cRepo, hRepo);
            if (i%2==1) {
                accountIds.add(account.getAccountId());
            }
        }
        // 2. Generate postings between several days and few accounts
        ZonedDateTime postedAt = ZonedDateTime.now();
        LocalDate reportedOn = LocalDate.now();
        for (int i=0;i<accountIds.size();i++) {
            final Long accountId = accountIds.get(i);
            final Account account = Account.getExisting(accountId, attrRepo, cRepo, hRepo);
            for (Long j=0L;j<2000;j++) {
                if (j%2==0) {
                    account.credit(j, postedAt, reportedOn, new BigDecimal("7.00"));
                } else {
                    account.debit(j, postedAt, reportedOn, new BigDecimal("3.00"));
                }
                postedAt = postedAt.plusMinutes(1);
                int days = (int)(j / 700);
                reportedOn = reportedOn.plusDays(days);
            }
        }
    }

    @Test
    void testBalance() throws LedgerException {
        // 3. Check the final balance
        final Account account = Account.getExisting(286L, attrRepo, cRepo, hRepo);
        final AccountAttributes attributes = account.getAttributes().getEntity();
        Assert.assertEquals(attributes.getBalance(), new BigDecimal("4000.00").negate());
        // 4. Check number of historical pages
    }
}