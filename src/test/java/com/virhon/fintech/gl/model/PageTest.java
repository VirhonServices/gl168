package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PageTest {

    public static Page createTestPage() throws LedgerException {
        final ZonedDateTime startedAt = ZonedDateTime.now().minusDays(3);
        final LocalDate reportedAt = LocalDate.now().minusDays(3);
        final BigDecimal startedBalance = new BigDecimal("10.282737364");
        final Page page = Page.create(startedAt, reportedAt, startedBalance, startedBalance);
        for (int i=0;i<85;i++) {
            final ZonedDateTime postedAt = startedAt.plusSeconds(i*i);
            final BigDecimal currentBalance =
                    startedBalance.add(new BigDecimal("1.29384858599").multiply(new BigDecimal(i)));
            final Transfer tr = new Transfer();
            tr.setTransferUuid(UUID.randomUUID().toString());
            tr.setTransferRef("TRANSFER-REF");
            tr.setPostedAt(postedAt);
            tr.setReportedOn(reportedAt);
            tr.setAmount(currentBalance);
            tr.setLocalAmount(currentBalance);
            page.addTransfer(tr);
        }
        return page;
    }

    @Test(enabled = true)
    void test() throws LedgerException {
        final ZonedDateTime startedAt = ZonedDateTime.now().minusDays(3);
        final LocalDate reportedAt = LocalDate.now().minusDays(3);
        final BigDecimal startedBalance = new BigDecimal("10.282737364");
        ZonedDateTime fixedDateTimeAt = null;
        BigDecimal fixedBalance = BigDecimal.ZERO;
        final Page page = Page.create(startedAt, reportedAt, startedBalance, startedBalance);
        for (int i=0;i<85;i++) {
            final ZonedDateTime postedAt = startedAt.plusSeconds(i*i);
            final BigDecimal currentBalance =
                    startedBalance.add(new BigDecimal("1.29384858599").multiply(new BigDecimal(i)));
            final Transfer tr = new Transfer();
            tr.setTransferUuid(UUID.randomUUID().toString());
            tr.setTransferRef("TRANSFER-REF");
            tr.setPostedAt(postedAt);
            tr.setReportedOn(reportedAt);
            tr.setAmount(currentBalance);
            tr.setLocalAmount(currentBalance);
            page.addTransfer(tr);
            if (i==5) {
                fixedDateTimeAt = postedAt;
            }
            if (i<=5) {
                fixedBalance = fixedBalance.add(currentBalance);
            }
        }
        final ZonedDateTime dateBefore = ZonedDateTime.now().minusDays(10);
        Assert.assertFalse(page.contains(dateBefore));
        final ZonedDateTime dateAfter = ZonedDateTime.now().minusDays(1);
        Assert.assertTrue(page.currentCanContain(dateAfter));
        final BigDecimal gottenBalance = page.getBalanceAt(fixedDateTimeAt);
        Assert.assertEquals(gottenBalance, fixedBalance.add(startedBalance));
    }
}
