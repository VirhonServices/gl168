package com.virhon.fintech.gl.api.reportingperiod;

import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.GeneralLedger;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(classes = Application.class)
public class ReportingPeriodControllerTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TestDataMacros macros;

    @Autowired
    private GeneralLedger gl;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testCollectReportingData() throws LedgerException {
        final String accountUuid = macros.getObjectUuid("PASSIVE_MULTI_HISTORY9");
        final Ledger ledger = gl.getLedger("UAH");
        final Account account = ledger.getExistingByUuid(accountUuid);

        final LocalDate startedOn = LocalDate.of(2020, 2, 26);
        final LocalDate finishedOn = startedOn.plusDays(0);
        final Ledger.ReportingCollection collection = ledger.collectReportingData(account.getAccountId(), startedOn, finishedOn);

        BigDecimal bal = collection.getStartBalance();
        final List<Post> posts = collection.getPosts();
        for (int i=0;i<collection.getPosts().size();i++) {
            bal = bal.add(posts.get(i).getAmount());
        }
        Assert.assertEquals(bal, collection.getFinishBalance());
        final LocalDate startedOn1 = LocalDate.of(2020, 2, 27);
        final LocalDate finishedOn1 = startedOn1.plusDays(0);
        final Ledger.ReportingCollection collection1 = ledger.collectReportingData(account.getAccountId(), startedOn1, finishedOn1);
    }
}