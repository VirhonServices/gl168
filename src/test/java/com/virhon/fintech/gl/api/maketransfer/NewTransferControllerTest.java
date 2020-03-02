package com.virhon.fintech.gl.api.maketransfer;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDate;
import com.virhon.fintech.gl.model.AccountAttributes;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
public class NewTransferControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = GsonConverter.create();

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private TestDataMacros macros;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void test201() throws Exception {
        final BigDecimal amount = new BigDecimal("5000.00");
        final BigDecimal repAmount = new BigDecimal("200.00");
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(21);
        final NewTransferRequestBody request = new NewTransferRequestBody();
        request.setTransferRef("TRANSFER-001");
        final String debitUuid = macros.getObjectUuid("ACTIVE2");
        final String creditUuid = macros.getObjectUuid("PASSIVE_EMPTY0");
        request.setClientCustomerId("ClientCustomerId must be here");
        request.setCreditAccountUuid(creditUuid);
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(reportedOn);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(debitUuid).concat("/transfers"))
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.credit.accUuid").value(creditUuid)
                );
        // TODO: 22.02.20 check balances after test data added
    }

    @Test
    void testNPE() throws Exception {
        final BigDecimal amount = new BigDecimal("5000.00");
        final BigDecimal repAmount = new BigDecimal("200.00");
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(15);
        final NewTransferRequestBody request = new NewTransferRequestBody();
        request.setTransferRef("TRANSFER-001");
        final String debitUuid = macros.getObjectUuid("ACTIVE2");
        final String creditUuid = macros.getObjectUuid("PASSIVE_EMPTY0");
        request.setClientCustomerId("ClientCustomerId must be here");
        request.setCreditAccountUuid(creditUuid);
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(null);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(debitUuid).concat("/transfers"))
                .content(req)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.code").value(160)
                );
    }

    @Test
    void testRedSaldo() throws Exception {
        final BigDecimal amount = new BigDecimal("5000.00");
        final BigDecimal repAmount = new BigDecimal("200.00");
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(15);
        final NewTransferRequestBody request = new NewTransferRequestBody();
        request.setTransferRef("TRANSFER-001");
        request.setTransferRef("TRANSFER-001");
        final String debitUuid = macros.getObjectUuid("PASSIVE_EMPTY1");
        final String creditUuid = macros.getObjectUuid("ACTIVE2");
        request.setClientCustomerId("ClientCustomerId must be here");
        request.setCreditAccountUuid(creditUuid);
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(reportedOn);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(debitUuid).concat("/transfers"))
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(200)
                );
    }
}