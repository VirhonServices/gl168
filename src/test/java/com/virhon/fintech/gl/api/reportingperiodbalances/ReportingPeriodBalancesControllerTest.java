package com.virhon.fintech.gl.api.reportingperiodbalances;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDate;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.GeneralLedger;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Post;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
public class ReportingPeriodBalancesControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                @Override
                public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                    if (value != null) {
                        out.value(value.toString());
                    } else {
                        out.value("null");
                    }
                }
                @Override
                public ZonedDateTime read(JsonReader in) throws IOException {
                    final String value = in.nextString();
                    if (value != null && !value.toLowerCase().equals("null")) {
                        return ZonedDateTime.parse(value);
                    } else {
                        return null;
                    }
                }
            })
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter out, LocalDate value) throws IOException {
                    if (value != null) {
                        out.value(value.toString());
                    } else {
                        out.value("null");
                    }
                }
                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    final String value = in.nextString();
                    if (value != null && !value.toLowerCase().equals("null")) {
                        return LocalDate.parse(value);
                    } else {
                        return null;
                    }
                }
            })
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .create();

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
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accountUuid).concat("/reporting/balance"))
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