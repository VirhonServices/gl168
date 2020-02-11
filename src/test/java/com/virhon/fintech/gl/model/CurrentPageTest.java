package com.virhon.fintech.gl.model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class CurrentPageTest {

    public static CurrentPage createTestPage() {
        final ZonedDateTime startedAt = ZonedDateTime.now().minusDays(3);
        final BigDecimal startedBalance = new BigDecimal("10.282737364");
        final CurrentPage page = new CurrentPage(startedAt, startedBalance);
        for (int i=0;i<85;i++) {
            final ZonedDateTime postedAt = startedAt.plusSeconds(i*i);
            final BigDecimal currentBalance =
                    startedBalance.add(new BigDecimal("1.29384858599").multiply(new BigDecimal(i)));
            final LocalDate reportedAt = postedAt.toLocalDate();
            final Post post = new Post(0L, postedAt, reportedAt, currentBalance);
            page.addPost(post);
        }
        return page;
    }

    @Test
    void test() {
        final ZonedDateTime startedAt = ZonedDateTime.now().minusDays(3);
        final BigDecimal startedBalance = new BigDecimal("10.282737364");
        ZonedDateTime fixedDateTimeAt = null;
        BigDecimal fixedBalance = BigDecimal.ZERO;
        final CurrentPage page = new CurrentPage(startedAt, startedBalance);
        for (int i=0;i<85;i++) {
            final ZonedDateTime postedAt = startedAt.plusSeconds(i*i);
            final BigDecimal currentBalance =
                    startedBalance.add(new BigDecimal("1.29384858599").multiply(new BigDecimal(i)));
            final LocalDate reportedAt = postedAt.toLocalDate();
            final Post post = new Post(0L, postedAt, reportedAt, currentBalance);
            page.addPost(post);
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
        Assert.assertTrue(page.contains(dateAfter));
        final BigDecimal gottenBalance = page.getBalanceAt(fixedDateTimeAt);
        Assert.assertEquals(gottenBalance, fixedBalance.add(startedBalance));
    }
}
