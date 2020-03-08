package com.virhon.fintech.gl.api.reportingperiod;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDate;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.*;
import com.virhon.fintech.gl.security.Authorizer;
import com.virhon.fintech.gl.security.SignatureChecker;
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
import java.time.ZonedDateTime;
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

    @Autowired
    private Authorizer authorizer;

    @Autowired
    private APIConfig config;

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

        final ZonedDateTime startDate = ZonedDateTime.parse(macros.getObjectUuid("START_DATE"));
        final LocalDate startedOn = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth());
        final LocalDate finishedOn = startedOn.plusDays(0);
        final Ledger.ReportingCollection collection = ledger.collectReportingData(account.getAccountId(), startedOn, finishedOn);

        BigDecimal bal = collection.getStartBalance();
        final List<Transfer> transfers = collection.getTransfers();
        for (int i=0;i<transfers.size();i++) {
            bal = bal.add(transfers.get(i).getAmount());
        }
        Assert.assertEquals(bal, collection.getFinishBalance());

        final LocalDate startedOn1 = startedOn.plusDays(1);
        final LocalDate finishedOn1 = startedOn1.plusDays(0);
        final Ledger.ReportingCollection collection1 = ledger.collectReportingData(account.getAccountId(), startedOn1, finishedOn1);

        Assert.assertEquals(collection.getFinishBalance(), collection1.getStartBalance());
    }

    @Test
    void testGet() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_MULTI_HISTORY9");
        final Ledger ledger = gl.getLedger("UAH");
        final Account account = ledger.getExistingByUuid(accountUuid);

        final ZonedDateTime startDate = ZonedDateTime.parse(macros.getObjectUuid("START_DATE")).plusDays(1);
        final LocalDate startedOn1 = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth());
        final LocalDate finishedOn1 = startedOn1.plusDays(0);
        final Ledger.ReportingCollection collection1 = ledger.collectReportingData(account.getAccountId(), startedOn1, finishedOn1);

        final SeparatedDate startedOn = new SeparatedDate();
        startedOn.setYear(startDate.getYear());
        startedOn.setMonth(startDate.getMonthValue());
        startedOn.setDay(startDate.getDayOfMonth());
        final SeparatedDate finishedOn = new SeparatedDate();
        finishedOn.setYear(startDate.getYear());
        finishedOn.setMonth(startDate.getMonthValue());
        finishedOn.setDay(startDate.getDayOfMonth());
        final ReportingPeriodRequest request = new ReportingPeriodRequest();
        request.setBeginOn(startedOn);
        request.setFinishOn(finishedOn);
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final AccountAttributes attr = account.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accountUuid).concat("/reporting"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.open.balance").value(collection1.getStartBalance().negate().doubleValue()))
                .andExpect(jsonPath("$.closed.balance").value(collection1.getFinishBalance().negate().doubleValue())
                );
    }

    @Test
    void testUnauthorized() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_MULTI_HISTORY9");
        final Ledger ledger = gl.getLedger("UAH");
        final Account account = ledger.getExistingByUuid(accountUuid);

        final ZonedDateTime startDate = ZonedDateTime.parse(macros.getObjectUuid("START_DATE")).plusDays(1);
        final LocalDate startedOn1 = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth());
        final LocalDate finishedOn1 = startedOn1.plusDays(0);
        final Ledger.ReportingCollection collection1 = ledger.collectReportingData(account.getAccountId(), startedOn1, finishedOn1);

        final SeparatedDate startedOn = new SeparatedDate();
        startedOn.setYear(startDate.getYear());
        startedOn.setMonth(startDate.getMonthValue());
        startedOn.setDay(startDate.getDayOfMonth());
        final SeparatedDate finishedOn = new SeparatedDate();
        finishedOn.setYear(startDate.getYear());
        finishedOn.setMonth(startDate.getMonthValue());
        finishedOn.setDay(startDate.getDayOfMonth());
        final ReportingPeriodRequest request = new ReportingPeriodRequest();
        request.setBeginOn(startedOn);
        request.setFinishOn(finishedOn);
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final AccountAttributes attr = account.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accountUuid).concat("/reporting"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, "wrong token")
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAccessDenied() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_MULTI_HISTORY9");
        final Ledger ledger = gl.getLedger("UAH");
        final Account account = ledger.getExistingByUuid(accountUuid);

        final ZonedDateTime startDate = ZonedDateTime.parse(macros.getObjectUuid("START_DATE")).plusDays(1);
        final LocalDate startedOn1 = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth());
        final LocalDate finishedOn1 = startedOn1.plusDays(0);
        final Ledger.ReportingCollection collection1 = ledger.collectReportingData(account.getAccountId(), startedOn1, finishedOn1);

        final SeparatedDate startedOn = new SeparatedDate();
        startedOn.setYear(startDate.getYear());
        startedOn.setMonth(startDate.getMonthValue());
        startedOn.setDay(startDate.getDayOfMonth());
        final SeparatedDate finishedOn = new SeparatedDate();
        finishedOn.setYear(startDate.getYear());
        finishedOn.setMonth(startDate.getMonthValue());
        finishedOn.setDay(startDate.getDayOfMonth());
        final ReportingPeriodRequest request = new ReportingPeriodRequest();
        request.setBeginOn(startedOn);
        request.setFinishOn(finishedOn);
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final AccountAttributes attr = account.getAttributes().getEntity();
        final String clientUuid = authorizer.get(3).getKey();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accountUuid).concat("/reporting"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}