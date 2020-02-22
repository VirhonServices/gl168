package com.virhon.fintech.gl.api.maketransfer;

import com.google.gson.Gson;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDate;
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
import static org.testng.Assert.*;

@SpringBootTest(classes = Application.class)
public class NewTransferControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = new Gson();

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

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
        request.setCreditAccountUuid("5e19fcbb-3fc5-497e-bcb9-09cf5e157fc6");
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(reportedOn);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/b105791b-2252-48fa-8558-8faa40a003bd/transfers")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.credit.accNumber").value("2602100009203")
                );
        // TODO: 22.02.20 check balances after test data added
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
        request.setCreditAccountUuid("b105791b-2252-48fa-8558-8faa40a003bd");
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(reportedOn);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/5e19fcbb-3fc5-497e-bcb9-09cf5e157fc6/transfers")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(200)
                );
    }
}