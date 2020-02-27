package com.virhon.fintech.gl.api.reportingperiod;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDate;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.GeneralLedger;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
public class ReportingPeriodControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = GsonConverter.create();

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
        final List<Transfer> transfers = collection.getTransfers();
        for (int i=0;i<transfers.size();i++) {
            bal = bal.add(transfers.get(i).getAmount());
        }
        Assert.assertEquals(bal, collection.getFinishBalance());

        final LocalDate startedOn1 = LocalDate.of(2020, 2, 27);
        final LocalDate finishedOn1 = startedOn1.plusDays(0);
        final Ledger.ReportingCollection collection1 = ledger.collectReportingData(account.getAccountId(), startedOn1, finishedOn1);

        Assert.assertEquals(collection.getFinishBalance(), collection1.getStartBalance());
    }

    @Test
    void testGet() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_MULTI_HISTORY9");
        final Ledger ledger = gl.getLedger("UAH");
        final Account account = ledger.getExistingByUuid(accountUuid);

        final LocalDate startedOn1 = LocalDate.of(2020, 2, 27);
        final LocalDate finishedOn1 = startedOn1.plusDays(0);
        final Ledger.ReportingCollection collection1 = ledger.collectReportingData(account.getAccountId(), startedOn1, finishedOn1);

        final SeparatedDate startedOn = new SeparatedDate();
        startedOn.setYear(2020);
        startedOn.setMonth(2);
        startedOn.setDay(27);
        final SeparatedDate finishedOn = new SeparatedDate();
        finishedOn.setYear(2020);
        finishedOn.setMonth(2);
        finishedOn.setDay(27);
        final ReportingPeriodRequest request = new ReportingPeriodRequest();
        request.setBeginOn(startedOn);
        request.setFinishOn(finishedOn);
        final String req = gson.toJson(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accountUuid).concat("/reporting"))
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.open.balance").value(collection1.getStartBalance().negate().doubleValue()))
                .andExpect(jsonPath("$.closed.balance").value(collection1.getFinishBalance().negate().doubleValue())
                );
    }
}