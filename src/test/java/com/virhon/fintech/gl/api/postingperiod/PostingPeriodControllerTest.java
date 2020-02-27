package com.virhon.fintech.gl.api.postingperiod;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDateTime;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.GeneralLedger;
import com.virhon.fintech.gl.model.Ledger;
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
import static org.testng.Assert.*;

@SpringBootTest(classes = Application.class)
public class PostingPeriodControllerTest extends AbstractTestNGSpringContextTests {
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
    void testPostingPeriod() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_MULTI_HISTORY9");
        final Ledger ledger = gl.getLedger("UAH");
        final Account account = ledger.getExistingByUuid(accountUuid);

        final SeparatedDateTime startedAt = new SeparatedDateTime();
        startedAt.setYear(2020);
        startedAt.setMonth(2);
        startedAt.setDay(26);
        startedAt.setHour(0);
        startedAt.setMinute(0);
        startedAt.setSecond(0);
        startedAt.setNano(0);
        final SeparatedDateTime finishedAt = new SeparatedDateTime();
        finishedAt.setYear(2020);
        finishedAt.setMonth(2);
        finishedAt.setDay(26);
        finishedAt.setHour(23);
        finishedAt.setMinute(59);
        finishedAt.setSecond(59);
        finishedAt.setNano(9999);
        final PostingPeriodRequest request = new PostingPeriodRequest();
        request.setStartedAt(startedAt);
        request.setFinishedAt(finishedAt);
        final String req = gson.toJson(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accountUuid).concat("/posting"))
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.open.balance").value(0));
    }
}