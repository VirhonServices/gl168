package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.Config;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.accountinformation.AccountInformationResponseBody;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.LedgerRepoFactory;
import com.virhon.fintech.gl.repo.mysql.MySQLLedgerRepoFactory;
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
import java.util.UUID;

public class AccountTest {
    private MySQLAttrRepo               attrRepo = new MySQLAttrRepo("uah_account_attribute");
    private MySQLCurrentPageRepo        cRepo = new MySQLCurrentPageRepo("uah_current_page");
    private MySQLHistoricalPageRepo     hRepo = new MySQLHistoricalPageRepo("uah_historical_page");

    private LedgerRepoFactory factory = new MySQLLedgerRepoFactory("UAH");
    private Ledger ledger = new Ledger(factory);

    private TestDataMacros macros = new TestDataMacros();

    public AccountTest() throws IOException {
        macros.init();
    }

    @Test(enabled = true)
    void testCreating() throws LedgerException {
        final List<Long> accountIds = new ArrayList<>();
        final List<BigDecimal> accountBalances = new ArrayList<>();
        // 1. Create new accounts
        for (Integer i=0;i<4;i++) {
            final String accountUuid = macros.getObjectUuid("PASSIVE_EMPTY".concat(i.toString()));
            final Account account = ledger.getExistingByUuid(accountUuid);
            final AccountAttributes attr = account.getAttributes().getEntity();
            if (i%2==1) {
                accountIds.add(account.getAccountId());
                accountBalances.add(attr.getBalance());
            }
        }
        // 2. Generate postings between several days and few accounts
        int limit = 20;
        Config.getInstance().setMaxNumPostsInBlock(limit / 2 + 3);
        int perDay = limit/3;
        ZonedDateTime postedAt = ZonedDateTime.now();
        LocalDate reportedOn = LocalDate.now();
        final BigDecimal seven = new BigDecimal("7.00");
        final BigDecimal three = new BigDecimal("3.00");
        for (int i=0;i<accountIds.size();i++) {
            final Long accountId = accountIds.get(i);
            final Account account = ledger.getExistingById(accountId);
            final AccountAttributes attr = account.getAttributes().getEntity();
            for (Long j=0L;j<limit;j++) {
                if (j%2==0) {
                    final Transfer tr = new Transfer();
                    tr.setTransferUuid(UUID.randomUUID().toString());
                    tr.setTransferRef("TRANSFER-REF");
                    tr.setPostedAt(ZonedDateTime.now());
                    tr.setDescription("Description");
                    tr.setReportedOn(LocalDate.now());
                    tr.setAmount(seven);
                    tr.setLocalAmount(seven);
                    tr.setDebitPageUuid(account.credit(tr));
                } else {
                    final Transfer tr = new Transfer();
                    tr.setTransferUuid(UUID.randomUUID().toString());
                    tr.setTransferRef("TRANSFER-REF");
                    tr.setPostedAt(ZonedDateTime.now());
                    tr.setDescription("Description");
                    tr.setReportedOn(LocalDate.now());
                    tr.setAmount(three);
                    tr.setLocalAmount(three);
                    tr.setDebitPageUuid(account.debit(tr));
                }
                postedAt = postedAt.plusMinutes(1);
                int days = (int)(j / perDay);
                reportedOn = reportedOn.plusDays(days);
            }
        }
        // 3. Check the final balance
        Account account = ledger.getExistingById(accountIds.get(0));
        final BigDecimal startBalance = accountBalances.get(0);
        final AccountAttributes attributes = account.getAttributes().getEntity();
        final BigDecimal targetBalance = new BigDecimal(limit * 2);
        Assert.assertTrue(attributes.getBalance().subtract(startBalance).compareTo(targetBalance.negate())==0);
/*
        // 4. Check number of historical pages
        final String accUuid = macros.getObjectUuid("PASSIVE_EMPTY5");
        account = ledger.getExistingByUuid(accUuid);
        final List<IdentifiedEntity<Page>> pages = hRepo.getHistory(account.getAccountId());
        final long pagesNum = pages.size();
        final int historicalPagesNumber = limit / Config.getInstance().getMaxNumPostsInBlock();
        Assert.assertEquals(pagesNum, historicalPagesNumber);
        final long curPageSize = limit - (pagesNum * Config.getInstance().getMaxNumPostsInBlock());
        final Page curPage = this.cRepo.getById(account.getAccountId()).getEntity();
        Assert.assertEquals(curPage.getTransfers().size(), curPageSize);
*/
    }
}
