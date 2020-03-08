package com.virhon.fintech.gl.api.postingperiod;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDateTime;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.model.GeneralLedger;
import com.virhon.fintech.gl.model.Ledger;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = Application.class)
public class PostingPeriodControllerTest extends AbstractTestNGSpringContextTests {
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
    APIConfig config;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testPostingPeriod() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_MULTI_HISTORY9");
        final ZonedDateTime startDate = ZonedDateTime.parse(macros.getObjectUuid("START_DATE"));
        final Ledger ledger = gl.getLedger("UAH");
        final Account account = ledger.getExistingByUuid(accountUuid);
        final SeparatedDateTime startedAt = new SeparatedDateTime();
        startedAt.setYear(startDate.getYear());
        startedAt.setMonth(startDate.getMonthValue());
        startedAt.setDay(startDate.getDayOfMonth());
        startedAt.setHour(0);
        startedAt.setMinute(0);
        startedAt.setSecond(0);
        startedAt.setNano(0);
        final SeparatedDateTime finishedAt = new SeparatedDateTime();
        finishedAt.setYear(startDate.getYear());
        finishedAt.setMonth(startDate.getMonthValue());
        finishedAt.setDay(startDate.getDayOfMonth());
        finishedAt.setHour(23);
        finishedAt.setMinute(59);
        finishedAt.setSecond(59);
        finishedAt.setNano(9999);
        final PostingPeriodRequest request = new PostingPeriodRequest();
        request.setStartedAt(startedAt);
        request.setFinishedAt(finishedAt);
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final AccountAttributes attr = account.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accountUuid).concat("/posting"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.open.balance").value(0));
    }

    @Test
    void testAccessDenied() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_MULTI_HISTORY9");
        final ZonedDateTime startDate = ZonedDateTime.parse(macros.getObjectUuid("START_DATE"));
        final Ledger ledger = gl.getLedger("UAH");
        final Account account = ledger.getExistingByUuid(accountUuid);
        final SeparatedDateTime startedAt = new SeparatedDateTime();
        startedAt.setYear(startDate.getYear());
        startedAt.setMonth(startDate.getMonthValue());
        startedAt.setDay(startDate.getDayOfMonth());
        startedAt.setHour(0);
        startedAt.setMinute(0);
        startedAt.setSecond(0);
        startedAt.setNano(0);
        final SeparatedDateTime finishedAt = new SeparatedDateTime();
        finishedAt.setYear(startDate.getYear());
        finishedAt.setMonth(startDate.getMonthValue());
        finishedAt.setDay(startDate.getDayOfMonth());
        finishedAt.setHour(23);
        finishedAt.setMinute(59);
        finishedAt.setSecond(59);
        finishedAt.setNano(9999);
        final PostingPeriodRequest request = new PostingPeriodRequest();
        request.setStartedAt(startedAt);
        request.setFinishedAt(finishedAt);
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final AccountAttributes attr = account.getAttributes().getEntity();
        final String clientUuid = authorizer.get(5).getKey();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accountUuid).concat("/posting"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthorized() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_MULTI_HISTORY9");
        final ZonedDateTime startDate = ZonedDateTime.parse(macros.getObjectUuid("START_DATE"));
        final Ledger ledger = gl.getLedger("UAH");
        final Account account = ledger.getExistingByUuid(accountUuid);
        final SeparatedDateTime startedAt = new SeparatedDateTime();
        startedAt.setYear(startDate.getYear());
        startedAt.setMonth(startDate.getMonthValue());
        startedAt.setDay(startDate.getDayOfMonth());
        startedAt.setHour(0);
        startedAt.setMinute(0);
        startedAt.setSecond(0);
        startedAt.setNano(0);
        final SeparatedDateTime finishedAt = new SeparatedDateTime();
        finishedAt.setYear(startDate.getYear());
        finishedAt.setMonth(startDate.getMonthValue());
        finishedAt.setDay(startDate.getDayOfMonth());
        finishedAt.setHour(23);
        finishedAt.setMinute(59);
        finishedAt.setSecond(59);
        finishedAt.setNano(9999);
        final PostingPeriodRequest request = new PostingPeriodRequest();
        request.setStartedAt(startedAt);
        request.setFinishedAt(finishedAt);
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final AccountAttributes attr = account.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accountUuid).concat("/posting"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, "wrong-token")
                .header(config.DATE_HEADER, "date")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}